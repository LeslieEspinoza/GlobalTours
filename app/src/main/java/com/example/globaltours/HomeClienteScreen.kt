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
        bottomBar = { NavegacionInferiorCliente(navController, "home_cliente") },
        containerColor = BackgroundBeige
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            HeaderSection(navController = navController)
            Spacer(modifier = Modifier.height(24.dp))
            MotorReservasSection()
            Spacer(modifier = Modifier.height(24.dp))
            IAPlannerSection(navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderSection(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("GlobalTours", color = BluePrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Explore the world", color = Color.Gray, fontSize = 14.sp)
        }
        IconButton(onClick = { navController.navigate("notificaciones") }) {
            BadgedBox(
                badge = {
                    Badge(containerColor = Color.Red) { Text("3", color = Color.White) }
                }
            ) {
                Icon(Icons.Default.Notifications, contentDescription = "Notificaciones", tint = BluePrimary)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotorReservasSection() {
    // 1. Estado para controlar qué pestaña está activa
    var tipoBusqueda by remember { mutableStateOf("Vuelos") }

    Text(
        text = "Motor de Reservas 24/7",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = TextDark,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    // 2. Chips dinámicos
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = tipoBusqueda == "Vuelos",
            onClick = { tipoBusqueda = "Vuelos" },
            label = { Text("Vuelos") },
            leadingIcon = { Icon(Icons.Default.Flight, null, modifier = Modifier.size(16.dp)) },
            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BluePrimary, selectedLabelColor = Color.White, selectedLeadingIconColor = Color.White)
        )
        FilterChip(
            selected = tipoBusqueda == "Hoteles",
            onClick = { tipoBusqueda = "Hoteles" },
            label = { Text("Hoteles") },
            leadingIcon = { Icon(Icons.Default.Hotel, null, modifier = Modifier.size(16.dp)) },
            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BluePrimary, selectedLabelColor = Color.White, selectedLeadingIconColor = Color.White)
        )
        FilterChip(
            selected = tipoBusqueda == "Excursiones",
            onClick = { tipoBusqueda = "Excursiones" },
            label = { Text("Excursiones") },
            leadingIcon = { Icon(Icons.Default.Map, null, modifier = Modifier.size(16.dp)) },
            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BluePrimary, selectedLabelColor = Color.White, selectedLeadingIconColor = Color.White)
        )
    }

    // 3. Tarjeta de Búsqueda que cambia según la selección
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            when (tipoBusqueda) {
                "Vuelos" -> FormularioVuelos()
                "Hoteles" -> FormularioHoteles()
                "Excursiones" -> FormularioExcursiones()
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* Lógica de búsqueda conectada a base de datos en el futuro */ },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Buscar Disponibilidad", color = BluePrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- SUB-FORMULARIOS ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioVuelos() {
    val textFieldColors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.LightGray)
    OutlinedTextField(value = "Madrid (MAD)", onValueChange = {}, label = { Text("Origen") }, leadingIcon = { Icon(Icons.Default.FlightTakeoff, null) }, modifier = Modifier.fillMaxWidth(), readOnly = true, shape = RoundedCornerShape(12.dp), colors = textFieldColors)
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(value = "Tokio (HND)", onValueChange = {}, label = { Text("Destino") }, leadingIcon = { Icon(Icons.Default.FlightLand, null) }, modifier = Modifier.fillMaxWidth(), readOnly = true, shape = RoundedCornerShape(12.dp), colors = textFieldColors)
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(value = "15 Nov - 30 Nov", onValueChange = {}, label = { Text("Fechas") }, leadingIcon = { Icon(Icons.Default.DateRange, null) }, modifier = Modifier.fillMaxWidth(), readOnly = true, shape = RoundedCornerShape(12.dp), colors = textFieldColors)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioHoteles() {
    val textFieldColors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.LightGray)
    OutlinedTextField(value = "Kyoto, Japón", onValueChange = {}, label = { Text("Destino o Hotel") }, leadingIcon = { Icon(Icons.Default.LocationOn, null) }, modifier = Modifier.fillMaxWidth(), readOnly = true, shape = RoundedCornerShape(12.dp), colors = textFieldColors)
    Spacer(modifier = Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value = "15 Nov", onValueChange = {}, label = { Text("Check-in") }, leadingIcon = { Icon(Icons.Default.Event, null) }, modifier = Modifier.weight(1f), readOnly = true, shape = RoundedCornerShape(12.dp), colors = textFieldColors)
        OutlinedTextField(value = "18 Nov", onValueChange = {}, label = { Text("Check-out") }, leadingIcon = { Icon(Icons.Default.Event, null) }, modifier = Modifier.weight(1f), readOnly = true, shape = RoundedCornerShape(12.dp), colors = textFieldColors)
    }
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(value = "2 Huéspedes, 1 Hab.", onValueChange = {}, label = { Text("Ocupación") }, leadingIcon = { Icon(Icons.Default.Person, null) }, modifier = Modifier.fillMaxWidth(), readOnly = true, shape = RoundedCornerShape(12.dp), colors = textFieldColors)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioExcursiones() {
    val textFieldColors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.LightGray)
    OutlinedTextField(value = "Monte Fuji", onValueChange = {}, label = { Text("Atracción o Ciudad") }, leadingIcon = { Icon(Icons.Default.LocationOn, null) }, modifier = Modifier.fillMaxWidth(), readOnly = true, shape = RoundedCornerShape(12.dp), colors = textFieldColors)
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(value = "16 Nov 2026", onValueChange = {}, label = { Text("Fecha de la experiencia") }, leadingIcon = { Icon(Icons.Default.Event, null) }, modifier = Modifier.fillMaxWidth(), readOnly = true, shape = RoundedCornerShape(12.dp), colors = textFieldColors)
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
fun NavegacionInferiorCliente(navController: NavController, rutaActual: String) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = BluePrimary
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = rutaActual == "home_cliente",
            onClick = { if (rutaActual != "home_cliente") navController.navigate("home_cliente") { popUpTo(0) } }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ConfirmationNumber, contentDescription = "Reservas") },
            label = { Text("Reservas") },
            selected = rutaActual == "reservas",
            onClick = { if (rutaActual != "reservas") navController.navigate("reservas") { popUpTo(0) } }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "IA Planner") },
            label = { Text("IA Planner") },
            selected = false,
            onClick = { navController.navigate("itinerario") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Luggage, contentDescription = "Mis Viajes") },
            label = { Text("Mis Viajes") },
            selected = rutaActual == "mis_viajes",
            onClick = { if (rutaActual != "mis_viajes") navController.navigate("mis_viajes") { popUpTo(0) } }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") },
            selected = rutaActual == "perfil",
            onClick = { if (rutaActual != "perfil") navController.navigate("perfil") { popUpTo(0) } }
        )
    }
}