package com.stadiolinks.kitabu.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Query("SELECT * FROM books ORDER BY title COLLATE NOCASE")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE isAvailable = :available ORDER BY title COLLATE NOCASE")
    fun getBooksByAvailability(available: Boolean): Flow<List<BookEntity>>

    @Query(
        """
        SELECT * FROM books
        WHERE title LIKE '%' || :query || '%' COLLATE NOCASE
           OR author LIKE '%' || :query || '%' COLLATE NOCASE
        ORDER BY title COLLATE NOCASE
        """
    )
    fun searchBooks(query: String): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE bookId = :bookId LIMIT 1")
    suspend fun getBookById(bookId: Int): BookEntity?

    @Update
    suspend fun updateBook(book: BookEntity)

    @Query("UPDATE books SET isAvailable = :isAvailable WHERE bookId = :bookId")
    suspend fun updateAvailability(bookId: Int, isAvailable: Boolean)

    @Delete
    suspend fun deleteBook(book: BookEntity)
}
