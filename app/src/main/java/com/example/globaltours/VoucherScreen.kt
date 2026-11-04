package com.example.globaltours

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherScreen(navController: NavController) {
    // Variable para controlar el diálogo de éxito
    var mostrarDialogoOffline by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pase de Abordaje VIP", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("home_cliente") {
                            popUpTo("home_cliente") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("¡Reserva Confirmada!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Presenta este código QR en tu llegada", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(32.dp))
            TicketCard()
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { mostrarDialogoOffline = true },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, tint = BluePrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Descargar PDF (Offline)", color = BluePrimary, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { mostrarDialogoOffline = true },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Wallet, contentDescription = null, tint = BluePrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Guardar en Apple/Google Wallet", color = BluePrimary, fontWeight = FontWeight.Bold)
            }
        }

        // Mostrar el diálogo si el estado es true
        if (mostrarDialogoOffline) {
            OfflineSuccessDialog(onDismiss = { mostrarDialogoOffline = false })
        }
    }
}

@Composable
fun TicketCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BluePrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                Text("GLOBAL TOURS BESPOKE", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Icon(Icons.Default.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("ESCAPADA KYOTO & TOKIO", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text("ID Reserva: #GT-8902X", color = Color.LightGray, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .drawBehind {
                        drawLine(
                            color = Color.Gray,
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                        )
                    }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Pasajero VIP", color = Color.Gray, fontSize = 12.sp)
                    Text("Titular de Cuenta", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Válido Desde", color = Color.Gray, fontSize = 12.sp)
                    Text("15 Nov 2026", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(180.dp)
                    .background(Color.White, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.QrCode2, contentDescription = "QR Code", tint = BluePrimary, modifier = Modifier.size(140.dp))
            }
        }
    }
}

@Composable
fun OfflineSuccessDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(64.dp).background(Color(0xFFE8F5E9), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Éxito", tint = Color(0xFF2E7D32), modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Guardado Correctamente", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Tu pase de abordaje ya está disponible para uso sin conexión a internet.", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { onDismiss() },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Entendido", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}