package com.stadiolinks.kitabu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.stadiolinks.kitabu.ui.KitabuViewModel
import com.stadiolinks.kitabu.ui.KitabuViewModelFactory
import com.stadiolinks.kitabu.ui.navigation.KitabuDestination
import com.stadiolinks.kitabu.ui.screens.CatalogScreen
import com.stadiolinks.kitabu.ui.screens.ReservationsScreen
import com.stadiolinks.kitabu.ui.theme.KitabuTheme
import androidx.compose.foundation.layout.padding

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as KitabuApplication
        val factory = KitabuViewModelFactory(app.container.libraryRepository)

        setContent {
            KitabuTheme {
                val viewModel: KitabuViewModel = viewModel(factory = factory)
                val catalogState by viewModel.catalogUiState.collectAsStateWithLifecycle()
                val reservationsState by viewModel.reservationsUiState.collectAsStateWithLifecycle()
                val message by viewModel.message.collectAsStateWithLifecycle()
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route

                LaunchedEffect(message) {
                    message?.let {
                        snackbarHostState.showSnackbar(it)
                        viewModel.clearMessage()
                    }
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    bottomBar = {
                        NavigationBar {
                            KitabuDestination.bottomNavItems.forEach { destination ->
                                NavigationBarItem(
                                    selected = currentRoute == destination.route,
                                    onClick = {
                                        navController.navigate(destination.route) {
                                            popUpTo(KitabuDestination.Catalog.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(destination.icon, contentDescription = destination.label) },
                                    label = { Text(destination.label) }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = KitabuDestination.Catalog.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(KitabuDestination.Catalog.route) {
                            CatalogScreen(
                                state = catalogState,
                                onSearchChange = viewModel::updateSearchQuery,
                                onFilterChange = viewModel::updateFilter,
                                onReserve = { book, name, days ->
                                    viewModel.reserveBook(book.bookId, name, days)
                                }
                            )
                        }
                        composable(KitabuDestination.Reservations.route) {
                            ReservationsScreen(
                                state = reservationsState,
                                onActivate = viewModel::activateBooking,
                                onRenew = viewModel::renewBooking,
                                onReturn = viewModel::returnBook,
                                onCancel = viewModel::cancelBooking
                            )
                        }
                    }
                }
            }
        }
    }
}
