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
fun PersonalizarScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Personalizar Itinerario", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Día 1: Kyoto Tradicional & Gion", color = Color.LightGray, fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                actions = {
                    Button(
                        onClick = { /* Acción Listo */ },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Listo", color = BluePrimary, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BluePrimary)
            )
        },
        bottomBar = { ConfirmarCambiosBottomBar(navController) },
        containerColor = BackgroundBeige
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OptimizadorCard()
            Spacer(modifier = Modifier.height(24.dp))
            Text("Alternativas Inteligentes Recomendadas", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(16.dp))
            AlternativaCard(
                titulo = "Té Privado en Daitoku-ji & Shojin Ryori",
                precio = "$150 USD",
                diferencia = "-$30 USD",
                tag = "99% AFINIDAD ZEN"
            )
            Spacer(modifier = Modifier.height(16.dp))
            AlternativaCard(
                titulo = "Taller Privado de Forja con Maestro",
                precio = "$265 USD",
                diferencia = "+$85 USD",
                tag = "TRENDING VIP"
            )
        }
    }
}

@Composable
fun OptimizadorCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = BluePrimary),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldAccent)
                Spacer(modifier = Modifier.width(8.dp))
                Text("OPTIMIZADOR INTELIGENTE ACTIVO", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Cada reemplazo recalcula tiempos de traslados y presupuesto sin generar colisiones horarias.", color = Color.LightGray, fontSize = 12.sp)
        }
    }
}

@Composable
fun AlternativaCard(titulo: String, precio: String, diferencia: String, tag: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(tag, color = GoldAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(diferencia, color = if (diferencia.startsWith("-")) Color(0xFF2E7D32) else Color(0xFFD32F2F), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(titulo, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Precio total: $precio", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { /* Seleccionar */ },
                colors = ButtonDefaults.buttonColors(containerColor = BackgroundBeige),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Seleccionar y Reemplazar", color = BluePrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ConfirmarCambiosBottomBar(navController: NavController) {
    BottomAppBar(containerColor = Color.White) {
        Button(
            onClick = { navController.navigate("confirmacion") },
            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Aplicar y Confirmar Cambios", color = BluePrimary, fontWeight = FontWeight.Bold)
        }
    }
}