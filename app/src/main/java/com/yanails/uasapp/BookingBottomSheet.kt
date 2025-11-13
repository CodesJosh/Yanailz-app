package com.yanails.uasapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BookingBottomSheet(vm: BookingViewModel) {
    val open by vm.sheetOpen
    val service by vm.serviceSelected
    val date by vm.dateSelected // <-- La fecha que el usuario selecciona
    val time by vm.timeSelected
    val scope = rememberCoroutineScope()

    if (!open || service == null) return

    ModalBottomSheet(
        onDismissRequest = { vm.closeBooking() },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Agendar ${service!!.title}", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text(service!!.description)
            Spacer(Modifier.height(16.dp))

            // Fecha
            DateSelector(
                date = date,
                onChange = vm::setDate
            )

            Spacer(Modifier.height(16.dp))

            // Hora
            // Ahora le pasamos el ViewModel y la fecha seleccionada (date)
            // para que sepa qué horas deshabilitar.
            TimeSelector(
                time = time,
                onChange = vm::setTime,
                vm = vm,
                selectedDate = date
            )

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    scope.launch {
                        vm.confirm()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar reserva")
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DateSelector(
    date: LocalDate,
    onChange: (LocalDate) -> Unit
) {
    // (Sin cambios aquí)
    Text("Selecciona la fecha:", style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(onClick = { onChange(date.minusDays(1)) }) { Text("←") }
        OutlinedButton(onClick = { onChange(LocalDate.now()) }) { Text("Hoy") }
        OutlinedButton(onClick = { onChange(date.plusDays(1)) }) { Text("→") }
    }
    Spacer(Modifier.height(8.dp))
    Text("Fecha: $date")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TimeSelector(
    time: LocalTime,
    onChange: (LocalTime) -> Unit,
    vm: BookingViewModel,
    selectedDate: LocalDate
) {
    val slots = listOf(9,10,11,12,14,15,16,17,18).map { LocalTime.of(it, 0) }

    Text("Selecciona la hora:", style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))

    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        slots.forEach { t ->S
            // Revisamos si esta hora (t) en este día (selectedDate) ya está tomada
            val isTaken = vm.isSlotTaken(selectedDate, t)

            FilterChip(
                selected = t == time,
                onClick = { onChange(t) },
                label = { Text("${t.hour}:00") },
                // La hora se deshabilita (enabled = false) si está tomada
                enabled = !isTaken
            )
        }
    }
}