package com.yanails.uasapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*

// Objeto para definir las "rutas" (como en una web)
object Routes {
    const val Home = "home"
    const val Services = "services"
    const val Agenda = "agenda"
    const val Profile = "profile"

    // Helper para los ítems de la barra
    data class BarItem(
        val route: String,
        val label: String,
        val icon: ImageVector
    )

    val barItems = listOf(
        BarItem(Home, "Inicio", Icons.Default.Home),
        BarItem(Services, "Servicios", Icons.Default.List),
        BarItem(Agenda, "Agenda", Icons.Default.DateRange),
        BarItem(Profile, "Perfil", Icons.Default.Person)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    // El controlador de navegación (recuerda la pantalla actual)
    val nav = rememberNavController()
    // El ViewModel (almacena la lista de reservas)
    val vm: BookingViewModel = viewModel()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentTitle(nav)) }
            )
        },
        bottomBar = {
            BottomBar(nav)
        },
        // El snackbar se "engancha" al del ViewModel
        snackbarHost = { SnackbarHost(vm.snackbar) }
    ) { inner ->
        // Este es el "Router" que cambia de pantalla
        NavHost(
            navController = nav,
            startDestination = Routes.Home,
            modifier = Modifier.padding(inner)
        ) {
            composable(Routes.Home) {
                HomeScreen(
                    onCTAClick = { nav.navigate(Routes.Services) }
                )
            }
            composable(Routes.Services) {
                ServicesScreen(
                    services = DemoData.services,
                    onBook = { service ->
                        // Al agendar, le decimos al ViewModel que abra la hoja
                        vm.openBooking(service)
                    }
                )
            }
            composable(Routes.Agenda) {
                AgendaScreen(
                    bookings = vm.bookings,
                    onCancel = vm::cancel
                )
            }
            composable(Routes.Profile) {
                ProfileScreen()
            }
        }
    }

    // Esta es la hoja (bottom sheet) que se muestra al agendar.
    // Está "fuera" del NavHost para que se superponga a todo.
    BookingBottomSheet(vm)
}

@Composable
private fun BottomBar(nav: NavHostController) {
    NavigationBar {
        val backStack by nav.currentBackStackEntryAsState()
        val current = backStack?.destination?.route

        Routes.barItems.forEach { item ->
            NavigationBarItem(
                selected = item.route == current,
                onClick = {
                    // Lógica para navegar sin duplicar pantallas
                    nav.navigate(item.route) {
                        popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

@Composable
private fun currentTitle(nav: NavHostController): String {
    val route = nav.currentBackStackEntryAsState().value?.destination?.route
    // Devuelve un título diferente para cada pantalla
    return when (route) {
        Routes.Home -> "YaNails"
        Routes.Services -> "Nuestros Servicios"
        Routes.Agenda -> "Mi Agenda"
        Routes.Profile -> "Perfil"
        else -> "YaNails"
    }
}