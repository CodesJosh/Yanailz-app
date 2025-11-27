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

object Routes {
    const val Login = "login"
    const val Home = "home"
    const val Services = "services"
    const val Agenda = "agenda"
    const val Profile = "profile"

    data class BarItem(val route: String, val label: String, val icon: ImageVector)

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
    val nav = rememberNavController()
    val vm: BookingViewModel = viewModel()

    // Controlamos en qué ruta estamos para saber si mostrar las barras
    var currentRoute by remember { mutableStateOf(Routes.Login) }
    val showBars = currentRoute != Routes.Login

    Scaffold(
        topBar = {
            if (showBars) {
                TopAppBar(title = { Text(currentTitle(nav)) })
            }
        },
        bottomBar = {
            if (showBars) {
                BottomBar(nav)
            }
        },
        snackbarHost = { SnackbarHost(vm.snackbar) }
    ) { inner ->
        NavHost(
            navController = nav,
            startDestination = Routes.Login,
            modifier = Modifier.padding(inner)
        ) {
            // Pantalla de Login
            composable(Routes.Login) {
                LaunchedEffect(Unit) { currentRoute = Routes.Login }

                LoginScreen(
                    vm = vm,
                    onLoginSuccess = {
                        nav.navigate(Routes.Home) {
                            popUpTo(Routes.Login) { inclusive = true }
                        }
                    }
                )
            }

            // Pantalla de Inicio
            composable(Routes.Home) {
                LaunchedEffect(Unit) { currentRoute = Routes.Home }
                HomeScreen(onCTAClick = { nav.navigate(Routes.Services) })
            }

            // Pantalla de Servicios
            composable(Routes.Services) {
                LaunchedEffect(Unit) { currentRoute = Routes.Services }
                ServicesScreen(
                    services = DemoData.services,
                    onBook = { service -> vm.openBooking(service) }
                )
            }

            // Pantalla de Agenda
            composable(Routes.Agenda) {
                LaunchedEffect(Unit) { currentRoute = Routes.Agenda }
                AgendaScreen(bookings = vm.bookings, onCancel = vm::cancel)
            }

            // Pantalla de Perfil
            composable(Routes.Profile) {
                LaunchedEffect(Unit) { currentRoute = Routes.Profile }
                ProfileScreen(vm = vm)
            }
        }
    }

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
    return when (route) {
        Routes.Home -> "YaNails"
        Routes.Services -> "Nuestros Servicios"
        Routes.Agenda -> "Mi Agenda"
        Routes.Profile -> "Perfil"
        else -> "YaNails"
    }
}