package com.stadiolinks.kitabu.data.local

import com.stadiolinks.kitabu.domain.model.BookingStatus

data class BookingWithBook(
    val bookingId: Int,
    val bookOwnerId: Int,
    val userName: String,
    val bookingDate: Long,
    val returnDeadline: Long,
    val status: BookingStatus,
    val bookTitle: String,
    val bookAuthor: String,
    val bookCategory: String
)
