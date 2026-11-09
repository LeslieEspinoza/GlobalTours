package com.example.globaltours

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherScreen(
    navController: NavController,
    reservaId: Long
) {
    var reserva by remember { mutableStateOf<ReservaModel?>(null) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(reservaId) {
        try {
            val res = SupabaseProvider.client.from("reservas")
                .select {
                    filter {
                        eq("id", reservaId)
                    }
                }.decodeSingleOrNull<ReservaModel>()
            reserva = res
        } catch (e: Exception) {
            android.util.Log.e("VOUCHER_ERROR", "Error al cargar voucher: ${e.message}", e)
        } finally {
            cargando = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comprobante de Viaje", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            if (cargando) {
                CircularProgressIndicator(color = BluePrimary, modifier = Modifier.align(Alignment.Center))
            } else if (reserva == null) {
                Text("No se encontró la información del comprobante.", color = Color.Gray, modifier = Modifier.align(Alignment.Center))
            } else {
                val item = reserva!!
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Cabecera del Boleto
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Reserva Confirmada", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Text("Código de Confirmación: GT-${item.id + 10000}", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)

                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(20.dp))

                        // Detalle del Servicio
                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Servicio:", color = Color.Gray, fontSize = 13.sp)
                                Text(item.tipo_servicio, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BluePrimary)
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Descripción:", color = Color.Gray, fontSize = 13.sp)
                                Text(item.titulo, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Detalles:", color = Color.Gray, fontSize = 13.sp)
                                Text(item.detalle, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = TextDark)
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Pagado:", color = Color.Gray, fontSize = 13.sp)
                                Text("$${item.precio.toInt()} USD", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = GoldAccent)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(24.dp))

                        // Representación del Código QR de Abordaje
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.QrCode2, contentDescription = "Código QR", tint = TextDark, modifier = Modifier.size(110.dp))
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Muestra este código al abordar o hacer Check-in", fontSize = 11.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { /* Acción para compartir/guardar PDF */ },
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Compartir / Descargar Voucher", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}