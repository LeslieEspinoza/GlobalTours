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
fun ItinerarioScreen(navController: NavController) {
    Scaffold(
        bottomBar = { ReservaBottomBar(navController) },
        containerColor = BackgroundBeige
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            HeaderDestino(navController)
            ResumenTags()
            CronogramaSection()
            DesgloseInversion()
        }
    }
}

@Composable
fun HeaderDestino(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BluePrimary)
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 24.dp)
    ) {
        Column {
            // Barra de navegación superior
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Escapada Kyoto & Tokio", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Borrador Inteligente Optimizado", color = Color.LightGray, fontSize = 10.sp)
                }

                Row {
                    Icon(Icons.Default.Share, contentDescription = "Compartir", tint = Color.White, modifier = Modifier.padding(end = 12.dp))
                    Icon(Icons.Default.BookmarkBorder, contentDescription = "Guardar", tint = Color.White)
                }
            }

            // Información del destino
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Shield, contentDescription = "Bespoke", tint = GoldAccent, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("JAPÓN - ITINERARIO BESPOKE", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Tradición Zen & Vanguardia Tokyoite", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("3 Días / 2 Noches • Experiencia Exclusiva", color = Color.LightGray, fontSize = 14.sp)
        }
    }
}

@Composable
fun ResumenTags() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TagCard("PRESUPUESTO", "$2,750 USD", Icons.Default.AttachMoney, Modifier.weight(1f))
        TagCard("RITMO", "Equilibrado", Icons.Default.DirectionsWalk, Modifier.weight(1f))
    }
}

@Composable
fun TagCard(titulo: String, valor: String, icono: ImageVector, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icono, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(titulo, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Text(valor, fontSize = 14.sp, color = TextDark, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CronogramaSection() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Cronograma Personalizado", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Día 1: Arribo a la Antigua Capital", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = BluePrimary)
        Spacer(modifier = Modifier.height(16.dp))

        TimelineItem("09:00", "Llegada a Kansai (KIX) & Traslado Privado", "Recepción personal por chofer bilingüe en sedán ejecutivo Mercedes Maybach.", "VIP Transfer", Icons.Default.DirectionsCar)
        TimelineItem("13:00", "Almuerzo Kaiseki de Bienvenida", "Menú degustación de 9 tiempos en restaurante galardonado con 2 Estrellas Michelin.", "Gastronomía", Icons.Default.Restaurant)
        TimelineItem("16:00", "Check-in: Hotel The Mitsui", "Alojamiento en Suite Nijo con Onsen termal privado de piedra natural.", "5 Estrellas", Icons.Default.Hotel)
    }
}

@Composable
fun TimelineItem(hora: String, titulo: String, desc: String, tag: String, icono: ImageVector) {
    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape).background(BackgroundBeige),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(16.dp))
            }
            Box(modifier = Modifier.width(2.dp).height(80.dp).background(Color.LightGray))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(hora, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(tag, color = GoldAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(titulo, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                Text(desc, fontSize = 12.sp, color = Color.DarkGray)
            }
        }
    }
}

@Composable
fun DesgloseInversion() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Desglose de Inversión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Hotelería 5* GL", fontSize = 14.sp, color = Color.Gray)
                Text("$1,400", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Shinkansen & Chófer", fontSize = 14.sp, color = Color.Gray)
                Text("$650", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Experiencias & Michelin", fontSize = 14.sp, color = Color.Gray)
                Text("$700", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ReservaBottomBar(navController: NavController) {
    BottomAppBar(
        containerColor = Color.White,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Precio Total
            Column {
                Text("TOTAL POR PERSONA", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Text("$2,750 USD", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
            }

            // Botones de acción
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(
                    onClick = { navController.navigate("personalizar") }, // <-- Navega a editar
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Editar", color = BluePrimary, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { navController.navigate("confirmacion") },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Reservar", color = BluePrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}