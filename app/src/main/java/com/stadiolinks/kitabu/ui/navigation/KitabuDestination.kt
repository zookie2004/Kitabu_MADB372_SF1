package com.stadiolinks.kitabu.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.ui.graphics.vector.ImageVector

sealed class KitabuDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Catalog : KitabuDestination("catalog", "Catalog", Icons.Default.Book)
    data object Reservations : KitabuDestination("reservations", "Reservations", Icons.Default.EventNote)

    companion object {
        val bottomNavItems = listOf(Catalog, Reservations)
    }
}
