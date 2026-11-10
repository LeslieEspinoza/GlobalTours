package com.example.globaltours

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservasScreen(navController: NavController) {
    var listaReservas by remember { mutableStateOf<List<ReservaModel>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var filtroEstado by remember { mutableStateOf("Todos") }
    val scope = rememberCoroutineScope()

    // Función para obtener las reservas actualizadas de Supabase
    fun cargarReservas() {
        cargando = true
        scope.launch {
            try {
                val userId = SupabaseProvider.client.auth.currentUserOrNull()?.id
                if (userId != null) {
                    val reservas = SupabaseProvider.client.from("reservas")
                        .select {
                            filter {
                                eq("auth_id", userId)
                            }
                        }.decodeList<ReservaModel>()
                    listaReservas = reservas
                }
            } catch (e: Exception) {
                android.util.Log.e("RESERVAS_SCREEN", "Error al obtener reservas: ${e.message}", e)
            } finally {
                cargando = false
            }
        }
    }

    // Función para cancelar la reserva en Supabase
    fun cancelarReserva(reservaId: Any) {
        cargando = true
        scope.launch {
            try {
                SupabaseProvider.client.from("reservas").update(
                    {
                        set("estado", "Cancelado")
                    }
                ) {
                    filter {
                        eq("id", reservaId)
                    }
                }
                cargarReservas() // Recargar la lista después de cancelar
            } catch (e: Exception) {
                android.util.Log.e("RESERVAS_SCREEN", "Error al cancelar reserva: ${e.message}", e)
                cargando = false
            }
        }
    }

    LaunchedEffect(Unit) {
        cargarReservas()
    }

    // Filtrar según el Chip seleccionado
    val reservasFiltradas = remember(listaReservas, filtroEstado) {
        if (filtroEstado == "Todos") {
            listaReservas
        } else {
            listaReservas.filter { it.estado.contains(filtroEstado, ignoreCase = true) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Reservas", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BluePrimary)
            )
        },
        bottomBar = { NavegacionInferiorCliente(navController, rutaActual = "reservas") },
        containerColor = BackgroundBeige
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Chips de filtrado por estado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filtroEstado == "Todos",
                    onClick = { filtroEstado = "Todos" },
                    label = { Text("Todos") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BluePrimary,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = filtroEstado == "Pendiente",
                    onClick = { filtroEstado = "Pendiente" },
                    label = { Text("Pendientes") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BluePrimary,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = filtroEstado == "Confirmado",
                    onClick = { filtroEstado = "Confirmado" },
                    label = { Text("Confirmados") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BluePrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }

            if (cargando) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BluePrimary)
                }
            } else if (reservasFiltradas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tienes reservas registradas en este estado.", color = Color.Gray, fontSize = 14.sp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(reservasFiltradas) { reserva ->
                        ReservaItemCard(
                            reserva = reserva,
                            onPagarAhora = {
                                navController.navigate("pasarela_pago?reservaId=${reserva.id}")
                            },
                            onCancelarReserva = {
                                cancelarReserva(reserva.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReservaItemCard(
    reserva: ReservaModel,
    onPagarAhora: () -> Unit,
    onCancelarReserva: () -> Unit
) {
    var mostrarDialogoCancelar by remember { mutableStateOf(false) }

    // Selección dinámica de icono según tipo de servicio
    val icono = when (reserva.tipo_servicio) {
        "Vuelo" -> Icons.Default.Flight
        "Hotel" -> Icons.Default.Hotel
        "Excursion", "Excursión" -> Icons.Default.Map
        else -> Icons.Default.Luggage
    }

    // Selección dinámica de color según el estado
    val (colorEstado, textoEstado) = when {
        reserva.estado.contains("Cancelad", ignoreCase = true) -> Pair(Color.Gray, "Cancelada")
        reserva.estado.contains("Pendiente", ignoreCase = true) -> Pair(Color(0xFFD32F2F), "Pendiente de Pago")
        else -> Pair(Color(0xFF2E7D32), "Confirmado")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(BackgroundBeige, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icono, contentDescription = null, tint = BluePrimary)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(reserva.titulo, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text(reserva.detalle, fontSize = 12.sp, color = Color.Gray)
                    }
                }
                Text(
                    text = "$${reserva.precio.toInt()}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BluePrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(textoEstado, color = colorEstado, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                // Botones de acción agrupados a la derecha
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                    // Botón de cancelar (Solo se muestra si no está ya cancelada)
                    if (!reserva.estado.contains("Cancelad", ignoreCase = true)) {
                        OutlinedButton(
                            onClick = { mostrarDialogoCancelar = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                            border = BorderStroke(1.dp, Color(0xFFD32F2F)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Cancelar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Botón de pagar ahora
                    if (reserva.estado.contains("Pendiente", ignoreCase = true)) {
                        Button(
                            onClick = onPagarAhora,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Pagar Ahora", color = BluePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Diálogo de confirmación para cancelar reserva
    if (mostrarDialogoCancelar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCancelar = false },
            title = { Text("Cancelar Reserva", fontWeight = FontWeight.Bold, color = BluePrimary) },
            text = { Text("¿Estás seguro de que deseas cancelar la reserva de '${reserva.titulo}'? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoCancelar = false
                        onCancelarReserva()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Sí, Cancelar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoCancelar = false }) {
                    Text("Volver", color = Color.Gray)
                }
            },
            containerColor = Color.White
        )
    }
}