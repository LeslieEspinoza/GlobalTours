package com.example.globaltours

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItinerarioIAScreen(
    navController: NavController,
    presupuestoInicial: Int = 2800
) {
    var presupuesto by remember { mutableFloatStateOf(presupuestoInicial.toFloat()) }
    var estiloSeleccionado by remember { mutableStateOf("Cultura y Gastronomía") }
    var cargando by remember { mutableStateOf(false) }
    var resultadoItinerario by remember { mutableStateOf<ItinerarioIARespuesta?>(null) }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val estilos = listOf("Playa y Relax", "Cultura y Gastronomía", "Aventura y Naturaleza", "Lujo Exclusivo")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("IA Planner - Curaduría Digital", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BluePrimary)
            )
        },
        containerColor = BackgroundBeige
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Panel de Configuración de la IA
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Personaliza tu consulta IA", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Presupuesto: $${presupuesto.roundToInt()} USD", fontWeight = FontWeight.Bold, color = BluePrimary)
                    Slider(
                        value = presupuesto,
                        onValueChange = { presupuesto = it },
                        valueRange = 500f..10000f,
                        steps = 18,
                        colors = SliderDefaults.colors(thumbColor = GoldAccent, activeTrackColor = BluePrimary)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Estilo de Viaje", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        estilos.chunked(2).forEach { rowEstilos ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                rowEstilos.forEach { estilo ->
                                    FilterChip(
                                        selected = estiloSeleccionado == estilo,
                                        onClick = { estiloSeleccionado = estilo },
                                        label = { Text(estilo, fontSize = 12.sp) },
                                        modifier = Modifier.weight(1f),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = BluePrimary,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            cargando = true
                            mensajeError = null
                            scope.launch {
                                try {
                                    val respuesta = GeminiService.generarItinerario(
                                        presupuesto = presupuesto.roundToInt(),
                                        estiloViaje = estiloSeleccionado
                                    )
                                    resultadoItinerario = respuesta
                                } catch (e: Exception) {
                                    mensajeError = "No se pudo generar el itinerario: ${e.message}"
                                } finally {
                                    cargando = false
                                }
                            }
                        },
                        enabled = !cargando,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (cargando) {
                            CircularProgressIndicator(color = BluePrimary, modifier = Modifier.size(24.dp))
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BluePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generar Itinerario con IA", color = BluePrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            mensajeError?.let { err ->
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))) {
                    Text(err, color = Color.Red, modifier = Modifier.padding(16.dp), fontSize = 13.sp)
                }
            }

            // Despliegue del resultado generado por la IA
            resultadoItinerario?.let { itinerario ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Propuesta Generada", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(itinerario.titulo_paquete, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Text(itinerario.destino, fontSize = 14.sp, color = BluePrimary, fontWeight = FontWeight.SemiBold)

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(itinerario.descripcion_general, fontSize = 14.sp, color = Color.DarkGray, lineHeight = 20.sp)

                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(color = BackgroundBeige, shape = RoundedCornerShape(8.dp)) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Hotel, contentDescription = null, tint = BluePrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Hospedaje Sugerido", fontSize = 11.sp, color = Color.Gray)
                                    Text(itinerario.hotel_sugerido, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Plan Día por Día", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Spacer(modifier = Modifier.height(8.dp))

                        itinerario.dias.forEach { dia ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Día ${dia.dia}: ${dia.titulo}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BluePrimary)
                                        Text("$${dia.costo_estimado_dia.toInt()} USD", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color.Gray)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    dia.actividades.forEach { act ->
                                        Text("• $act", fontSize = 13.sp, color = TextDark, modifier = Modifier.padding(vertical = 2.dp))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Botón de Reserva del Plan IA
                        Button(
                            onClick = {
                                scope.launch {
                                    try {
                                        val userId = SupabaseProvider.client.auth.currentUserOrNull()?.id
                                        if (userId != null) {
                                            val nuevaReserva = ReservaModel(
                                                auth_id = userId,
                                                tipo_servicio = "Paquete IA",
                                                titulo = itinerario.titulo_paquete,
                                                detalle = "${itinerario.duracion_dias} Días en ${itinerario.destino} (${estiloSeleccionado})",
                                                precio = itinerario.presupuesto_total_estimado,
                                                estado = "Pendiente de Pago"
                                            )
                                            SupabaseProvider.client.from("reservas").insert(nuevaReserva)
                                            navController.navigate("reservas")
                                        }
                                    } catch (e: Exception) {
                                        android.util.Log.e("RESERVA_IA_ERROR", "Error: ${e.message}", e)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Bookmark, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Guardar y Reservar este Itinerario", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}