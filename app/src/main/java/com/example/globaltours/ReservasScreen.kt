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
fun ReservasScreen(navController: NavController) {
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Cotizaciones Pendientes", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(16.dp))
            ReservaItemCard(
                tipoServicio = "Vuelo Primera Clase",
                detalle = "MAD -> HND (Ida y Vuelta)",
                estado = "Pendiente de Pago",
                precio = "$2,100",
                icono = Icons.Default.Flight,
                colorEstado = Color(0xFFD32F2F) // Rojo para pendiente
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Servicios Confirmados", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(16.dp))
            ReservaItemCard(
                tipoServicio = "Hotel 5 Estrellas",
                detalle = "The Mitsui Kyoto (3 Noches)",
                estado = "Confirmado",
                precio = "$1,400",
                icono = Icons.Default.Hotel,
                colorEstado = Color(0xFF2E7D32) // Verde para confirmado
            )
        }
    }
}

@Composable
fun ReservaItemCard(tipoServicio: String, detalle: String, estado: String, precio: String, icono: androidx.compose.ui.graphics.vector.ImageVector, colorEstado: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(40.dp).background(BackgroundBeige, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icono, contentDescription = null, tint = BluePrimary)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(tipoServicio, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Text(detalle, fontSize = 12.sp, color = Color.Gray)
                    }
                }
                Text(precio, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(estado, color = colorEstado, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                if (estado == "Pendiente de Pago") {
                    Button(
                        onClick = { /* Navegar a pago */ },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Pagar Ahora", color = BluePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}