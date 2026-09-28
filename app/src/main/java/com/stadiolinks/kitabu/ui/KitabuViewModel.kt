package com.stadiolinks.kitabu.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stadiolinks.kitabu.domain.model.CatalogFilter
import com.stadiolinks.kitabu.domain.repository.LibraryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class KitabuViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val catalogFilter = MutableStateFlow(CatalogFilter.ALL)

    private val booksFlow = searchQuery.flatMapLatest { query ->
        if (query.isBlank()) repository.observeAllBooks()
        else repository.searchBooks(query)
    }

    val catalogUiState: StateFlow<CatalogUiState> = combine(
        booksFlow,
        searchQuery,
        catalogFilter
    ) { books, query, filter ->
        val filtered = when (filter) {
            CatalogFilter.ALL -> books
            CatalogFilter.AVAILABLE -> books.filter { it.isAvailable }
            CatalogFilter.BORROWED -> books.filterNot { it.isAvailable }
        }
        CatalogUiState(
            books = filtered,
            searchQuery = query,
            filter = filter
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CatalogUiState()
    )

    val reservationsUiState: StateFlow<ReservationsUiState> =
        repository.observeCurrentBookings()
            .map { bookings -> ReservationsUiState(bookings) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ReservationsUiState()
            )

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun updateSearchQuery(value: String) {
        searchQuery.value = value.take(80)
    }

    fun updateFilter(filter: CatalogFilter) {
        catalogFilter.value = filter
    }

    fun reserveBook(bookId: Int, userName: String, rentalDays: Int) = runAction(
        successMessage = "Book reserved. It is now pending pickup."
    ) {
        repository.reserveBook(bookId, userName, rentalDays)
    }

    fun activateBooking(bookingId: Int) = runAction(
        successMessage = "Reservation activated."
    ) {
        repository.activateBooking(bookingId)
    }

    fun renewBooking(bookingId: Int) = runAction(
        successMessage = "Rental extended by 7 days."
    ) {
        repository.renewBooking(bookingId, 7)
    }

    fun returnBook(bookingId: Int) = runAction(
        successMessage = "Book returned and marked available."
    ) {
        repository.returnBook(bookingId)
    }

    fun cancelBooking(bookingId: Int) = runAction(
        successMessage = "Pending reservation cancelled."
    ) {
        repository.cancelPendingBooking(bookingId)
    }

    fun clearMessage() {
        _message.value = null
    }

    private fun runAction(successMessage: String, action: suspend () -> Unit) {
        viewModelScope.launch {
            runCatching { action() }
                .onSuccess { _message.value = successMessage }
                .onFailure { error -> _message.value = error.message ?: "Something went wrong." }
        }
    }
}
