package com.stadiolinks.kitabu.domain.repository

import com.stadiolinks.kitabu.data.local.BookEntity
import com.stadiolinks.kitabu.data.local.BookingWithBook
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {
    fun observeAllBooks(): Flow<List<BookEntity>>
    fun observeBooksByAvailability(available: Boolean): Flow<List<BookEntity>>
    fun searchBooks(query: String): Flow<List<BookEntity>>
    fun observeCurrentBookings(): Flow<List<BookingWithBook>>

    suspend fun reserveBook(bookId: Int, userName: String, rentalDays: Int)
    suspend fun activateBooking(bookingId: Int)
    suspend fun renewBooking(bookingId: Int, extraDays: Int = 7)
    suspend fun returnBook(bookingId: Int)
    suspend fun cancelPendingBooking(bookingId: Int)
}
