package com.example.globaltours

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
fun MisViajesScreen(navController: NavController) {
    var tabSeleccionada by remember { mutableIntStateOf(0) }
    var reservas by remember { mutableStateOf<List<ReservaModel>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val userId = SupabaseProvider.client.auth.currentUserOrNull()?.id
                if (userId != null) {
                    val lista = SupabaseProvider.client.from("reservas")
                        .select {
                            filter {
                                eq("auth_id", userId)
                            }
                        }.decodeList<ReservaModel>()
                    reservas = lista
                }
            } catch (e: Exception) {
                android.util.Log.e("MIS_VIAJES_ERROR", "Error: ${e.message}", e)
            } finally {
                cargando = false
            }
        }
    }

    val proximosViajes = reservas.filter {
        it.estado.contains("Confirmad", ignoreCase = true) || it.estado.contains("Pendiente", ignoreCase = true)
    }
    val historialViajes = reservas.filter {
        it.estado.contains("Completad", ignoreCase = true) || it.estado.contains("Finalizad", ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Viajes", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BluePrimary)
            )
        },
        bottomBar = {
            NavegacionInferiorCliente(navController = navController, rutaActual = "mis_viajes")
        },
        containerColor = BackgroundBeige
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Pestañas (Tabs)
            TabRow(
                selectedTabIndex = tabSeleccionada,
                containerColor = Color.White,
                contentColor = BluePrimary
            ) {
                Tab(
                    selected = tabSeleccionada == 0,
                    onClick = { tabSeleccionada = 0 },
                    text = { Text("Próximos (${proximosViajes.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = tabSeleccionada == 1,
                    onClick = { tabSeleccionada = 1 },
                    text = { Text("Historial (${historialViajes.size})", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (cargando) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BluePrimary)
                }
            } else {
                val listaAMostrar = if (tabSeleccionada == 0) proximosViajes else historialViajes

                if (listaAMostrar.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.FlightTakeoff, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                if (tabSeleccionada == 0) "No tienes viajes próximos programados" else "No tienes viajes en tu historial",
                                color = Color.Gray,
                                fontSize = 15.sp
                            )
                        }
                    }
                } else {
                    // ⚠️ SE AGREGA fillMaxSize() PARA EVITAR ALTURA INFINITA EN EL LAZYCOLUMN
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(listaAMostrar) { viaje ->
                            TarjetaViajeItem(
                                viaje = viaje,
                                onClickVoucher = { navController.navigate("voucher?id=${viaje.id}") }
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun TarjetaViajeItem(
    viaje: ReservaModel,
    onClickVoucher: () -> Unit
) {
    val estaPagado = viaje.estado.contains("Confirmad", ignoreCase = true) ||
            viaje.estado.contains("Completad", ignoreCase = true)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = BluePrimary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = viaje.tipo_servicio.uppercase(),
                        color = BluePrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    color = if (estaPagado) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = viaje.estado,
                        color = if (estaPagado) Color(0xFF2E7D32) else Color(0xFFE65100),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(viaje.titulo, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(viaje.detalle, fontSize = 13.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${viaje.precio.toInt()} USD",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BluePrimary
                )

                // Botón deshabilitado si no está pagado
                Button(
                    onClick = onClickVoucher,
                    enabled = estaPagado,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BluePrimary,
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFE0E0E0),
                        disabledContentColor = Color(0xFF9E9E9E)
                    )
                ) {
                    Icon(
                        imageVector = if (estaPagado) Icons.Default.ConfirmationNumber else Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (estaPagado) "Ver Voucher" else "Pago Pendiente",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}