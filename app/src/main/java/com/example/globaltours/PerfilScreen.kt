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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BluePrimary)
            )
        },
        bottomBar = { NavegacionInferiorCliente(navController, rutaActual = "perfil") },
        containerColor = BackgroundBeige
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header del Usuario
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(BluePrimary),
                contentAlignment = Alignment.Center
            ) {
                Text("JD", color = GoldAccent, fontSize = 36.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Juan Doe", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text("juan.doe@example.com", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))

            // Tarjeta de Gamificación VIP
            VipProgressCard()

            Spacer(modifier = Modifier.height(24.dp))

            // Opciones del menú
            MenuOptionItem(icono = Icons.Default.Person, titulo = "Mis Datos Personales") { navController.navigate("mis_datos") }
            MenuOptionItem(icono = Icons.Default.Favorite, titulo = "Preferencias de Viaje") { navController.navigate("preferencias") }
            MenuOptionItem(icono = Icons.Default.Payment, titulo = "Métodos de Pago") { navController.navigate("metodos_pago") }
            MenuOptionItem(icono = Icons.Default.SupportAgent, titulo = "Chat de Soporte VIP") { navController.navigate("chat_soporte") }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = {
                    // Lógica para cerrar sesión y volver al login
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun VipProgressCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BluePrimary),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WorkspacePremium, contentDescription = "VIP", tint = GoldAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Miembro Oro", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Text("2,450 pts", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { 0.75f }, // 75% de progreso (simulado)
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = GoldAccent,
                trackColor = Color(0xFF1A3F5C)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Te faltan 550 puntos para ser Miembro Platino",
                color = Color.LightGray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun MenuOptionItem(icono: ImageVector, titulo: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icono, contentDescription = null, tint = BluePrimary)
            Spacer(modifier = Modifier.width(16.dp))
            Text(titulo, fontSize = 16.sp, color = TextDark, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}