package com.stadiolinks.kitabu.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stadiolinks.kitabu.data.local.BookingWithBook
import com.stadiolinks.kitabu.domain.model.BookingStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun BookingCard(
    booking: BookingWithBook,
    onActivate: () -> Unit,
    onRenew: () -> Unit,
    onReturn: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val daysRemaining = calculateDaysRemaining(booking.returnDeadline)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = booking.bookTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(10.dp))
                StatusBadge(booking.status)
            }

            Text("Author: ${booking.bookAuthor}")
            Text("Category: ${booking.bookCategory}")
            Text("Borrower: ${booking.userName}")
            Text("Reserved: ${formatDate(booking.bookingDate)}")
            Text("Return deadline: ${formatDate(booking.returnDeadline)}")

            val dayText = when {
                daysRemaining > 1 -> "$daysRemaining days remaining"
                daysRemaining == 1L -> "1 day remaining"
                daysRemaining == 0L -> "Due today"
                else -> "${-daysRemaining} day(s) overdue"
            }
            Text(
                text = dayText,
                fontWeight = FontWeight.Bold,
                color = if (daysRemaining < 0) Color(0xFFC62828) else MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(4.dp))
            if (booking.status == BookingStatus.PENDING) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(onClick = onActivate) { Text("Pick Up") }
                    OutlinedButton(onClick = onCancel) { Text("Cancel") }
                }
            } else if (booking.status == BookingStatus.ACTIVE) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilledTonalButton(onClick = onRenew) { Text("Renew +7d") }
                    Button(onClick = onReturn) { Text("Return") }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: BookingStatus) {
    val (background, foreground) = when (status) {
        BookingStatus.PENDING -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        BookingStatus.ACTIVE -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
        BookingStatus.RETURNED -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
    }

    Surface(shape = RoundedCornerShape(50), color = background) {
        Text(
            text = status.name,
            color = foreground,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
        )
    }
}

private fun formatDate(timestamp: Long): String =
    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(timestamp))

private fun calculateDaysRemaining(deadline: Long): Long {
    val difference = deadline - System.currentTimeMillis()
    return TimeUnit.MILLISECONDS.toDays(difference)
}
