package com.stadiolinks.kitabu.data.repository

import androidx.room.withTransaction
import com.stadiolinks.kitabu.data.local.BookDao
import com.stadiolinks.kitabu.data.local.BookEntity
import com.stadiolinks.kitabu.data.local.BookingDao
import com.stadiolinks.kitabu.data.local.BookingEntity
import com.stadiolinks.kitabu.data.local.BookingWithBook
import com.stadiolinks.kitabu.data.local.KitabuDatabase
import com.stadiolinks.kitabu.domain.model.BookingStatus
import com.stadiolinks.kitabu.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

class OfflineLibraryRepository(
    private val database: KitabuDatabase,
    private val bookDao: BookDao,
    private val bookingDao: BookingDao
) : LibraryRepository {

    override fun observeAllBooks(): Flow<List<BookEntity>> = bookDao.getAllBooks()

    override fun observeBooksByAvailability(available: Boolean): Flow<List<BookEntity>> =
        bookDao.getBooksByAvailability(available)

    override fun searchBooks(query: String): Flow<List<BookEntity>> =
        bookDao.searchBooks(query.trim())

    override fun observeCurrentBookings(): Flow<List<BookingWithBook>> =
        bookingDao.getCurrentBookingsWithBook()

    override suspend fun reserveBook(bookId: Int, userName: String, rentalDays: Int) {
        val cleanName = userName.trim().replace(Regex("\\s+"), " ")
        require(cleanName.length in 2..60) { "Enter a valid name (2-60 characters)." }
        require(rentalDays in 1..30) { "Rental period must be between 1 and 30 days." }

        database.withTransaction {
            val book = bookDao.getBookById(bookId)
                ?: error("The selected book could not be found.")
            require(book.isAvailable) { "This book has already been reserved." }

            val now = System.currentTimeMillis()
            val deadline = now + TimeUnit.DAYS.toMillis(rentalDays.toLong())

            bookingDao.insertBooking(
                BookingEntity(
                    bookOwnerId = bookId,
                    userName = cleanName,
                    bookingDate = now,
                    returnDeadline = deadline,
                    status = BookingStatus.PENDING
                )
            )
            bookDao.updateAvailability(bookId, false)
        }
    }

    override suspend fun activateBooking(bookingId: Int) {
        database.withTransaction {
            val booking = bookingDao.getBookingById(bookingId)
                ?: error("Booking not found.")
            require(booking.status == BookingStatus.PENDING) { "Only pending reservations can be activated." }
            bookingDao.updateStatus(bookingId, BookingStatus.ACTIVE)
        }
    }

    override suspend fun renewBooking(bookingId: Int, extraDays: Int) {
        require(extraDays in 1..30) { "Renewal period must be between 1 and 30 days." }
        database.withTransaction {
            val booking = bookingDao.getBookingById(bookingId)
                ?: error("Booking not found.")
            require(booking.status == BookingStatus.ACTIVE) { "Only active rentals can be renewed." }

            val base = maxOf(booking.returnDeadline, System.currentTimeMillis())
            val newDeadline = base + TimeUnit.DAYS.toMillis(extraDays.toLong())
            bookingDao.updateDeadline(bookingId, newDeadline)
        }
    }

    override suspend fun returnBook(bookingId: Int) {
        database.withTransaction {
            val booking = bookingDao.getBookingById(bookingId)
                ?: error("Booking not found.")
            require(booking.status == BookingStatus.ACTIVE) { "Only active rentals can be returned." }

            bookingDao.updateStatus(bookingId, BookingStatus.RETURNED)
            bookDao.updateAvailability(booking.bookOwnerId, true)
        }
    }

    override suspend fun cancelPendingBooking(bookingId: Int) {
        database.withTransaction {
            val booking = bookingDao.getBookingById(bookingId)
                ?: error("Booking not found.")
            require(booking.status == BookingStatus.PENDING) { "Only pending reservations can be cancelled." }

            bookingDao.deleteBooking(booking)
            bookDao.updateAvailability(booking.bookOwnerId, true)
        }
    }
}
