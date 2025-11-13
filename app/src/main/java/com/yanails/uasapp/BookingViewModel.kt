package com.yanails.uasapp

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

// --- 1. MODELOS DE DATOS ---
data class Service(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val priceCLP: Int,
    val imageUrl: String
)

data class Booking(
    val id: String = UUID.randomUUID().toString(),
    val service: Service,
    val date: LocalDate,
    val time: LocalTime
)

// --- 2. DATOS ---
object DemoData {
    val services = listOf(
        Service(
            title = "Manicura Completa",
            description = "Servicio desde $5.000.",
            priceCLP = 5000,
            imageUrl = "https://images.unsplash.com/photo-1505682634904-d7c68f5388af?q=80&w=800"
        ),
        Service(
            title = "Esmaltado Express",
            description = "Servicio desde $8.000.",
            priceCLP = 8000,
            imageUrl = "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?q=80&w=800"
        ),
        Service(
            title = "Esmaltado Permanente",
            description = "Servicio desde $10.000.",
            priceCLP = 10000,
            imageUrl = "https://images.unsplash.com/photo-1580136579312-94651dfd596d?q=80&w=800"
        ),
        Service(
            title = "Realce Acrílico",
            description = "Servicio desde $15.000.",
            priceCLP = 15000,
            imageUrl = "https://images.unsplash.com/photo-1582092728068-13a5b3956d2d?q=80&w=800"
        ),
        Service(
            title = "Kapping Polygel",
            description = "Servicio desde $15.000.",
            priceCLP = 15000,
            imageUrl = "https://images.unsplash.com/photo-1604902396788-a896f21f681c?q=80&w=800"
        ),
        Service(
            title = "Acrílicas Esculpidas",
            description = "Servicio desde $22.000.",
            priceCLP = 22000,
            imageUrl = "https://images.unsplash.com/photo-1604902396788-a896f21f681c?q=80&w=800"
        ),
        Service(
            title = "Acrílicas con Tips",
            description = "Servicio desde $20.000.",
            priceCLP = 20000,
            imageUrl = "https://images.unsplash.com/photo-1582092728068-13a5b3956d2d?q=80&w=800"
        ),
        Service(
            title = "Soft Gel (Gel X)",
            description = "Servicio desde $18.000.",
            priceCLP = 18000,
            imageUrl = "https://images.unsplash.com/photo-1505682634904-d7c68f5388af?q=80&w=800"
        ),
        Service(
            title = "Pedicura Permanente",
            description = "Servicio desde $15.000.",
            priceCLP = 15000,
            imageUrl = "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?q=80&w=800"
        ),
        Service(
            title = "Acripie",
            description = "Servicio desde $20.000.",
            priceCLP = 20000,
            imageUrl = "https://images.unsplash.com/photo-1580136579312-94651dfd596d?q=80&w=800"
        ),
        Service(
            title = "Solo Retiro",
            description = "Servicio desde $7.000.",
            priceCLP = 7000,
            imageUrl = "https://images.unsplash.com/photo-1457972729786-22d4a654050b?q=80&w=800"
        ),
        Service(
            title = "Retiro de Otro Lugar",
            description = "Servicio desde $10.000.",
            priceCLP = 10000,
            imageUrl = "https://images.unsplash.com/photo-1457972729786-22d4a654050b?q=80&w=800"
        )
    )
}

// --- 3. VIEWMODEL ---

class BookingViewModel : ViewModel() {
    val bookings = mutableStateListOf<Booking>()
    val snackbar = SnackbarHostState()
    var sheetOpen = mutableStateOf(false)
        private set
    var serviceSelected = mutableStateOf<Service?>(null)
        private set
    var dateSelected = mutableStateOf(LocalDate.now())
        private set
    var timeSelected = mutableStateOf(LocalTime.of(10, 0))
        private set

    /**
     * Revisa si una hora específica en un día específico ya está reservada.
     */
    fun isSlotTaken(date: LocalDate, time: LocalTime): Boolean {
        // Busca en la lista de reservas si alguna coincide con la fecha Y la hora
        return bookings.any { it.date == date && it.time == time }
    }

    fun openBooking(service: Service) {
        serviceSelected.value = service
        dateSelected.value = LocalDate.now()
        timeSelected.value = LocalTime.of(10, 0)
        sheetOpen.value = true
    }

    fun closeBooking() {
        sheetOpen.value = false
    }

    suspend fun confirm() {
        val s = serviceSelected.value ?: return
        val d = dateSelected.value
        val t = timeSelected.value

        // --- ¡VALIDACIÓN AÑADIDA! ---
        // Doble chequeo al momento de confirmar, por si acaso
        if (isSlotTaken(d, t)) {
            snackbar.showSnackbar("Lo sentimos, esa hora (${t.hour}:00) ya fue tomada.")
            return // No se crea la reserva
        }

        bookings.add(
            Booking(
                service = s,
                date = d,
                time = t
            )
        )
        closeBooking()
        snackbar.showSnackbar("Reserva creada para ${s.title}")
    }

    fun cancel(id: String) {
        bookings.removeAll { it.id == id }
    }

    fun setDate(d: LocalDate) { dateSelected.value = d }
    fun setTime(t: LocalTime) { timeSelected.value = t }
}