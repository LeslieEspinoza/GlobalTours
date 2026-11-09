package com.example.globaltours

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePaqueteScreen(
    navController: NavController,
    paqueteId: Long
) {
    var paquete by remember { mutableStateOf<PaqueteTuristico?>(null) }
    var cargando by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(paqueteId) {
        try {
            val resultado = SupabaseProvider.client.from("paquetes_turisticos")
                .select {
                    filter {
                        eq("id", paqueteId)
                    }
                }.decodeSingleOrNull<PaqueteTuristico>()
            paquete = resultado
        } catch (e: Exception) {
            android.util.Log.e("DETALLE_PAQUETE", "Error al cargar detalle: ${e.message}", e)
        } finally {
            cargando = false
        }
    }

    Scaffold(
        bottomBar = {
            paquete?.let { p ->
                Surface(
                    shadowElevation = 8.dp,
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Precio Total", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                text = "$${p.precio_base.toInt()} USD",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = BluePrimary
                            )
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    try {
                                        val userId = SupabaseProvider.client.auth.currentUserOrNull()?.id
                                        if (userId != null) {
                                            val nuevaReserva = ReservaModel(
                                                auth_id = userId,
                                                tipo_servicio = "Paquete",
                                                titulo = p.titulo,
                                                detalle = "${p.duracion_dias} Días en ${p.destino}",
                                                precio = p.precio_base,
                                                estado = "Pendiente de Pago"
                                            )
                                            SupabaseProvider.client.from("reservas").insert(nuevaReserva)
                                            navController.navigate("reservas") {
                                                popUpTo("home_cliente")
                                            }
                                        }
                                    } catch (e: Exception) {
                                        android.util.Log.e("RESERVA_PAQUETE_ERROR", "Error: ${e.message}", e)
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(50.dp)
                                .padding(start = 16.dp)
                        ) {
                            Text("Reservar Ahora", color = BluePrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        },
        containerColor = BackgroundBeige
    ) { paddingValues ->
        if (cargando) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BluePrimary)
            }
        } else if (paquete == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No se encontró la información de este paquete.", color = Color.Gray)
            }
        } else {
            val item = paquete!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Imagen con Botón de Regreso Integrado
                Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
                    AsyncImage(
                        model = item.imagen_url,
                        contentDescription = item.titulo,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.4f))
                            .align(Alignment.TopStart)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                }

                // Cuerpo del Detalle
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = item.titulo,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(item.destino, color = Color.Gray, fontSize = 14.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${item.duracion_dias} Días", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                    // Descripción
                    Text("Descripción del Viaje", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text(
                        text = item.descripcion ?: "Disfruta de una experiencia inolvidable diseñada a tu medida con alojamiento de alta categoría, transporte privado y asistencia guiada en todo momento.",
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        lineHeight = 20.sp
                    )

                    // Incluye
                    Text("Qué incluye este paquete", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)

                    val servicios = item.incluye?.split(",") ?: listOf(
                        "Vuelos redondos incluidos",
                        "Hospedaje en hoteles seleccionados",
                        "Desayunos buffet diarios",
                        "Tours y excursiones guiadas",
                        "Seguro de viaje internacional"
                    )

                    servicios.forEach { servicio ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(servicio.trim(), fontSize = 14.sp, color = TextDark)
                        }
                    }
                }
            }
        }
    }
}