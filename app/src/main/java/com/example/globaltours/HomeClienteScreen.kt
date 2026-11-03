package com.example.globaltours

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HomeClienteScreen(navController: NavController) {
    Scaffold(
        bottomBar = { BottomNavigationBar() },
        containerColor = BackgroundBeige
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            HeaderSection()
            Spacer(modifier = Modifier.height(24.dp))
            MotorReservasSection()
            Spacer(modifier = Modifier.height(24.dp))
            IAPlannerSection(navController)
        }
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("GlobalTours", color = BluePrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Explore the world", color = Color.Gray, fontSize = 14.sp)
        }
        IconButton(onClick = { /* Notificaciones */ }) {
            Icon(Icons.Default.Notifications, contentDescription = "Notificaciones", tint = BluePrimary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotorReservasSection() {
    Text(
        text = "Motor de Reservas 24/7",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = TextDark,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    // Pestañas de Vuelos, Hoteles, Excursiones
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(selected = true, onClick = {}, label = { Text("Vuelos") }, leadingIcon = { Icon(Icons.Default.Flight, null, modifier = Modifier.size(16.dp)) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BluePrimary, selectedLabelColor = Color.White, selectedLeadingIconColor = Color.White))
        FilterChip(selected = false, onClick = {}, label = { Text("Hoteles") }, leadingIcon = { Icon(Icons.Default.Hotel, null, modifier = Modifier.size(16.dp)) })
        FilterChip(selected = false, onClick = {}, label = { Text("Excursiones") }, leadingIcon = { Icon(Icons.Default.Map, null, modifier = Modifier.size(16.dp)) })
    }

    // Tarjeta de Búsqueda
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(value = "Madrid (MAD)", onValueChange = {}, label = { Text("Origen") }, leadingIcon = { Icon(Icons.Default.FlightTakeoff, null) }, modifier = Modifier.fillMaxWidth(), readOnly = true, shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.LightGray))
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = "Tokio (TKO)", onValueChange = {}, label = { Text("Destino") }, leadingIcon = { Icon(Icons.Default.FlightLand, null) }, modifier = Modifier.fillMaxWidth(), readOnly = true, shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.LightGray))
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* Lógica de búsqueda */ },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Buscar Disponibilidad", color = BluePrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun IAPlannerSection(navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BluePrimary),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Alta Curaduría Digital", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text("Itinerarios Asistidos por IA", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

            Text("Presupuesto Estimado: $2,800 USD", color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
            Slider(value = 0.5f, onValueChange = {}, colors = SliderDefaults.colors(thumbColor = GoldAccent, activeTrackColor = GoldAccent, inactiveTrackColor = Color.Gray))

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate("itinerario") },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Generar Borrador con IA", color = BluePrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun BottomNavigationBar() {
    NavigationBar(
        containerColor = Color.White,
        contentColor = BluePrimary
    ) {
        NavigationBarItem(icon = { Icon(Icons.Default.Home, contentDescription = "Home") }, label = { Text("Home") }, selected = true, onClick = {})
        NavigationBarItem(icon = { Icon(Icons.Default.ConfirmationNumber, contentDescription = "Reservas") }, label = { Text("Reservas") }, selected = false, onClick = {})
        NavigationBarItem(icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "IA Planner") }, label = { Text("IA Planner") }, selected = false, onClick = {})
        NavigationBarItem(icon = { Icon(Icons.Default.Luggage, contentDescription = "Mis Viajes") }, label = { Text("Mis Viajes") }, selected = false, onClick = {})
        NavigationBarItem(icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") }, label = { Text("Perfil") }, selected = false, onClick = {})
    }
}