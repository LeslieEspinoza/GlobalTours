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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacionesScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NotificacionCard(
                titulo = "Check-in Disponible",
                mensaje = "Tu vuelo a Tokio JL-001 ya está abierto para check-in.",
                tiempo = "Hace 2 horas",
                icono = Icons.Default.FlightTakeoff,
                colorIcono = BluePrimary,
                noLeida = true
            )
            NotificacionCard(
                titulo = "Tu Itinerario IA está listo",
                mensaje = "Hemos diseñado tu ruta personalizada por Kyoto.",
                tiempo = "Hace 5 horas",
                icono = Icons.Default.AutoAwesome,
                colorIcono = GoldAccent,
                noLeida = true
            )
            NotificacionCard(
                titulo = "Recordatorio de Pago",
                mensaje = "Tu reserva en el Hotel Mitsui vence en 24 horas.",
                tiempo = "Ayer",
                icono = Icons.Default.Payment,
                colorIcono = Color(0xFFD32F2F),
                noLeida = false
            )
        }
    }
}

@Composable
fun NotificacionCard(titulo: String, mensaje: String, tiempo: String, icono: ImageVector, colorIcono: Color, noLeida: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (noLeida) Color.White else Color(0xFFFAFAFA)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (noLeida) 2.dp else 0.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).background(BackgroundBeige, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, contentDescription = null, tint = colorIcono)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(titulo, fontSize = 16.sp, fontWeight = if (noLeida) FontWeight.Bold else FontWeight.Normal, color = TextDark)
                    if (noLeida) {
                        Box(modifier = Modifier.size(8.dp).background(Color.Red, CircleShape))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(mensaje, fontSize = 14.sp, color = Color.DarkGray, lineHeight = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(tiempo, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}