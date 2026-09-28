package com.stadiolinks.kitabu.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stadiolinks.kitabu.data.local.BookEntity

@Composable
fun ReservationDialog(
    book: BookEntity,
    onDismiss: () -> Unit,
    onReserve: (userName: String, rentalDays: Int) -> Unit
) {
    var userName by remember { mutableStateOf("") }
    var rentalDays by remember { mutableIntStateOf(14) }
    val isNameValid = userName.trim().length >= 2

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reserve ${book.title}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Choose the rental duration and enter the borrower name.")
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it.take(60) },
                    label = { Text("Borrower name") },
                    singleLine = true,
                    supportingText = {
                        if (userName.isNotBlank() && !isNameValid) {
                            Text("Enter at least 2 characters.")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Rental duration")
                listOf(7, 14, 21).forEach { days ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = rentalDays == days,
                            onClick = { rentalDays = days }
                        )
                        Text("$days days", modifier = Modifier.padding(start = 6.dp))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = isNameValid,
                onClick = { onReserve(userName.trim(), rentalDays) }
            ) {
                Text("Reserve")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
