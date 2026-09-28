package com.stadiolinks.kitabu.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stadiolinks.kitabu.ui.ReservationsUiState
import com.stadiolinks.kitabu.ui.components.BookingCard

@Composable
fun ReservationsScreen(
    state: ReservationsUiState,
    onActivate: (Int) -> Unit,
    onRenew: (Int) -> Unit,
    onReturn: (Int) -> Unit,
    onCancel: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "My Reservations",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 14.dp)
        )
        Text(
            text = "Track pending pickups, active rentals and return deadlines.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        if (state.bookings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No active or pending reservations yet.")
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 92.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.bookings, key = { it.bookingId }) { booking ->
                    BookingCard(
                        booking = booking,
                        onActivate = { onActivate(booking.bookingId) },
                        onRenew = { onRenew(booking.bookingId) },
                        onReturn = { onReturn(booking.bookingId) },
                        onCancel = { onCancel(booking.bookingId) }
                    )
                }
            }
        }
    }
}
