package com.stadiolinks.kitabu.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stadiolinks.kitabu.data.local.BookEntity
import com.stadiolinks.kitabu.domain.model.CatalogFilter
import com.stadiolinks.kitabu.ui.CatalogUiState
import com.stadiolinks.kitabu.ui.components.BookCard
import com.stadiolinks.kitabu.ui.components.ReservationDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    state: CatalogUiState,
    onSearchChange: (String) -> Unit,
    onFilterChange: (CatalogFilter) -> Unit,
    onReserve: (BookEntity, String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedBook by remember { mutableStateOf<BookEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Book Catalog",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 14.dp)
        )
        Text(
            text = "Browse, search and reserve available library books.",
            style = MaterialTheme.typography.bodyMedium
        )

        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = onSearchChange,
            label = { Text("Search by title or author") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        androidx.compose.foundation.layout.Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CatalogFilter.entries.forEach { filter ->
                FilterChip(
                    selected = state.filter == filter,
                    onClick = { onFilterChange(filter) },
                    label = { Text(filter.label) }
                )
            }
        }

        if (state.books.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No books match your current search/filter.")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 165.dp),
                contentPadding = PaddingValues(bottom = 92.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.books, key = { it.bookId }) { book ->
                    BookCard(
                        book = book,
                        onClick = { selectedBook = book }
                    )
                }
            }
        }
    }

    selectedBook?.let { book ->
        ReservationDialog(
            book = book,
            onDismiss = { selectedBook = null },
            onReserve = { name, days ->
                onReserve(book, name, days)
                selectedBook = null
            }
        )
    }
}
