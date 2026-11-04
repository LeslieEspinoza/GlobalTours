package com.example.globaltours

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
        ) {
            PerfilHeader()
            Spacer(modifier = Modifier.height(16.dp))
            MenuOpcionesCard()
            Spacer(modifier = Modifier.height(24.dp))
            CerrarSesionBoton(navController)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PerfilHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BluePrimary)
            .padding(bottom = 32.dp, top = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(60.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Usuario VIP", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("usuario@globaltours.com", color = Color.LightGray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = GoldAccent),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Nivel: Bespoke Elite", color = BluePrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
            }
        }
    }
}

@Composable
fun MenuOpcionesCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            OpcionMenu(Icons.Default.ManageAccounts, "Datos Personales")
            HorizontalDivider(color = BackgroundBeige, modifier = Modifier.padding(horizontal = 16.dp))
            OpcionMenu(Icons.Default.Payment, "Métodos de Pago")
            HorizontalDivider(color = BackgroundBeige, modifier = Modifier.padding(horizontal = 16.dp))
            OpcionMenu(Icons.Default.FavoriteBorder, "Preferencias de Viaje")
            HorizontalDivider(color = BackgroundBeige, modifier = Modifier.padding(horizontal = 16.dp))
            OpcionMenu(Icons.Default.SupportAgent, "Concierge 24/7 (Soporte)")
        }
    }
}

@Composable
fun OpcionMenu(icono: ImageVector, titulo: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Futuras acciones de menú */ }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(titulo, fontSize = 16.sp, color = TextDark, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
    }
}

@Composable
fun CerrarSesionBoton(navController: NavController) {
    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        OutlinedButton(
            onClick = {
                // popUpTo(0) limpia el historial para no poder volver atrás con la flecha del celular
                navController.navigate("login") { popUpTo(0) }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)) // Rojo
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
        }
    }
}