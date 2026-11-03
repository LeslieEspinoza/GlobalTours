package com.example.globaltours

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmacionScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmación y Pago", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BluePrimary)
            )
        },
        bottomBar = { PagoBottomBar(navController) },
        containerColor = BackgroundBeige
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            ResumenViajeCard()
            Spacer(modifier = Modifier.height(16.dp))
            DesgloseFinalCard()
            Spacer(modifier = Modifier.height(16.dp))
            MetodoPagoCard()
        }
    }
}

@Composable
fun ResumenViajeCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.FlightTakeoff, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Escapada Kyoto & Tokio", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Text("3 Días / 2 Noches • 2 Personas", fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun DesgloseFinalCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Resumen de Costos", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(16.dp))

            FilaCosto("Itinerario Base (x2)", "$5,500.00")
            FilaCosto("Mejoras de Personalización", "+$170.00")
            FilaCosto("Impuestos y Tasas", "$450.00")

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Final (USD)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                Text("$6,120.00", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
            }
        }
    }
}

@Composable
fun FilaCosto(concepto: String, monto: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(concepto, fontSize = 14.sp, color = Color.DarkGray)
        Text(monto, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
    }
}

@Composable
fun MetodoPagoCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Método de Pago", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CreditCard, contentDescription = null, tint = BluePrimary)
                Spacer(modifier = Modifier.width(12.dp))
                Text("•••• •••• •••• 4242", fontSize = 16.sp, color = TextDark, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { /* Cambiar tarjeta */ }) {
                    Text("Cambiar", color = BluePrimary)
                }
            }
        }
    }
}

@Composable
fun PagoBottomBar(navController: NavController) {
    BottomAppBar(containerColor = Color.White) {
        Button(
            onClick = { navController.navigate("voucher") },
            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Pagar de Forma Segura", color = BluePrimary, fontWeight = FontWeight.Bold)
        }
    }
}