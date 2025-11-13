package com.yanails.uasapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onCTAClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "YaNails",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("Aquí foto principal")
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onCTAClick,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Ver Servicios y Agendar", fontSize = 18.sp)
        }
        Spacer(Modifier.height(24.dp))
        Text("Servicios populares", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        // Muestra solo los 2 primeros servicios en Home
        DemoData.services.take(2).forEach { s ->
            ServiceCard(service = s, onBook = { onCTAClick() })
        }
    }
}

// --- PANTALLA 2: SERVICIOS ---

@Composable
fun ServicesScreen(
    services: List<Service>,
    onBook: (Service) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(services) { s ->
            ServiceCard(service = s, onBook = { onBook(s) })
        }
    }
}

// --- PANTALLA 3: AGENDA ---

@Composable
fun AgendaScreen(
    bookings: List<Booking>,
    onCancel: (String) -> Unit
) {
    if (bookings.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Aún no tienes reservas")
        }
        return
    }

    // Formateadores para la fecha y hora
    val df = DateTimeFormatter.ofPattern("EEE d MMM")
    val tf = DateTimeFormatter.ofPattern("HH:mm")

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(bookings, key = { it.id }) { b ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(b.service.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("${df.format(b.date)} • ${tf.format(b.time)}")
                    }
                    TextButton(onClick = { onCancel(b.id) }) { Text("Cancelar") }
                }
            }
        }
    }
}

// --- PANTALLA 4: PERFIL ---

@Composable
fun ProfileScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Pronto: perfil, dirección y métodos de pago")
    }
}

// --- COMPONENTE REUTILIZABLE: TARJETA DE SERVICIO ---

@Composable
fun ServiceCard(
    service: Service,
    onBook: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column {
            // AsyncImage carga la imagen desde la URL
            AsyncImage(
                model = service.imageUrl,
                contentDescription = service.title,
                modifier = Modifier.fillMaxWidth().height(160.dp),
                contentScale = ContentScale.Crop // Recorta la imagen para llenar el espacio
            )
            Column(Modifier.padding(16.dp)) {
                Text(service.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(service.description, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
                Text("CLP ${service.priceCLP}", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                Button(onClick = onBook, modifier = Modifier.fillMaxWidth()) {
                    Text("Agendar")
                }
            }
        }
    }
}