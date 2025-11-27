package com.yanails.uasapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BookingBottomSheet(vm: BookingViewModel) {
    val open by vm.sheetOpen
    val service by vm.serviceSelected
    val date by vm.dateSelected
    val time by vm.timeSelected
    val payFull by vm.payFullAmount
    val isProcessing by vm.isProcessingPayment
    val scope = rememberCoroutineScope()

    if (!open || service == null) return

    ModalBottomSheet(
        onDismissRequest = { if (!isProcessing) vm.closeBooking() },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(service!!.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Valor Total: $${service!!.priceCLP}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

            Spacer(Modifier.height(16.dp))

            Text("1. Selecciona Fecha y Hora", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            DateSelector(date = date, onChange = vm::setDate)
            Spacer(Modifier.height(8.dp))
            TimeSelector(time = time, onChange = vm::setTime, vm = vm, selectedDate = date)

            Spacer(Modifier.height(24.dp))

            Text("2. Selecciona método de pago", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            val abono = (service!!.priceCLP * 0.25).toInt()
            val total = service!!.priceCLP

            PaymentOptionItem(
                title = "Abonar el 25%",
                amount = abono,
                selected = !payFull,
                onClick = { vm.setPaymentOption(false) }
            )

            Spacer(Modifier.height(8.dp))

            PaymentOptionItem(
                title = "Pagar Total",
                amount = total,
                selected = payFull,
                onClick = { vm.setPaymentOption(true) }
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        vm.processPaymentAndConfirm()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE91E63)
                ),
                enabled = !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Procesando pago seguro...", color = Color.White)
                } else {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    val montoFinal = if (payFull) total else abono
                    Text("Pagar $$montoFinal con Webpay", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text("Pagos procesados por Transbank Webpay Plus", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun PaymentOptionItem(title: String, amount: Int, selected: Boolean, onClick: () -> Unit) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else Color.LightGray
    val bgColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .background(bgColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Spacer(Modifier.width(8.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold)
            Text("Total a pagar ahora: $$amount", style = MaterialTheme.typography.bodyMedium)
        }
        if (selected) {
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun DateSelector(
    date: LocalDate,
    onChange: (LocalDate) -> Unit
) {
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

    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        slots.forEach { t ->
            val isTaken = vm.isSlotTaken(selectedDate, t)
            FilterChip(
                selected = t == time,
                onClick = { onChange(t) },
                label = { Text("${t.hour}:00") },
                enabled = !isTaken
            )
        }
    }
}