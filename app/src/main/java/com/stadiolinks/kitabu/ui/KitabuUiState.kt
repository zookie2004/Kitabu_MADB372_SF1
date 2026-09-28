package com.stadiolinks.kitabu.ui

import com.stadiolinks.kitabu.data.local.BookEntity
import com.stadiolinks.kitabu.data.local.BookingWithBook
import com.stadiolinks.kitabu.domain.model.CatalogFilter

data class CatalogUiState(
    val books: List<BookEntity> = emptyList(),
    val searchQuery: String = "",
    val filter: CatalogFilter = CatalogFilter.ALL
)

data class ReservationsUiState(
    val bookings: List<BookingWithBook> = emptyList()
)
