package com.stadiolinks.kitabu.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.stadiolinks.kitabu.domain.model.BookingStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBooking(booking: BookingEntity): Long

    @Query(
        """
        SELECT 
            bk.bookingId AS bookingId,
            bk.bookOwnerId AS bookOwnerId,
            bk.userName AS userName,
            bk.bookingDate AS bookingDate,
            bk.returnDeadline AS returnDeadline,
            bk.status AS status,
            b.title AS bookTitle,
            b.author AS bookAuthor,
            b.category AS bookCategory
        FROM bookings bk
        INNER JOIN books b ON b.bookId = bk.bookOwnerId
        WHERE bk.status IN ('PENDING', 'ACTIVE')
        ORDER BY bk.returnDeadline ASC
        """
    )
    fun getCurrentBookingsWithBook(): Flow<List<BookingWithBook>>

    @Query("SELECT * FROM bookings WHERE bookingId = :bookingId LIMIT 1")
    suspend fun getBookingById(bookingId: Int): BookingEntity?

    @Update
    suspend fun updateBooking(booking: BookingEntity)

    @Query("UPDATE bookings SET returnDeadline = :newDeadline WHERE bookingId = :bookingId")
    suspend fun updateDeadline(bookingId: Int, newDeadline: Long)

    @Query("UPDATE bookings SET status = :status WHERE bookingId = :bookingId")
    suspend fun updateStatus(bookingId: Int, status: BookingStatus)

    @Delete
    suspend fun deleteBooking(booking: BookingEntity)
}
