package com.example.globaltours

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.PaddingValues

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisViajesScreen(navController: NavController) {
    var tabSeleccionado by remember { mutableStateOf(0) }
    val tabs = listOf("Próximos", "Historial")

    // Simulamos listas vacías (En el futuro esto vendrá de Supabase)
    val viajesProximos = emptyList<String>()
    val historialViajes = emptyList<String>()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Viajes", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BluePrimary)
            )
        },
        bottomBar = { NavegacionInferiorCliente(navController, rutaActual = "mis_viajes") },
        containerColor = BackgroundBeige
    ) { paddingValues: PaddingValues -> // <- Tipo explícito añadido aquí
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = tabSeleccionado,
                containerColor = Color.White,
                contentColor = BluePrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[tabSeleccionado]),
                        color = GoldAccent,
                        height = 3.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, titulo ->
                    Tab(
                        selected = tabSeleccionado == index,
                        onClick = { tabSeleccionado = index },
                        text = { Text(titulo, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            // CONTENIDO RESTAURADO DE LAS PESTAÑAS
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                if (tabSeleccionado == 0) {
                    if (viajesProximos.isEmpty()) {
                        EmptyStateView(
                            icono = Icons.Default.Luggage,
                            titulo = "¿Aún no tienes viajes?",
                            subtitulo = "El mundo te espera. Crea un itinerario con IA o busca tu próximo destino.",
                            textoBoton = "Explorar Destinos",
                            onClickBoton = { navController.navigate("home_cliente") }
                        )
                    } else {
                        ViajeCard(
                            titulo = "Escapada Kyoto & Tokio",
                            fecha = "15 Nov - 18 Nov 2026",
                            estado = "CONFIRMADO",
                            colorEstado = Color(0xFF2E7D32),
                            onClickVoucher = { navController.navigate("voucher") }
                        )
                    }
                } else {
                    if (historialViajes.isEmpty()) {
                        EmptyStateView(
                            icono = Icons.Default.History,
                            titulo = "Tu historial está vacío",
                            subtitulo = "Aquí aparecerán los viajes inolvidables que hayas completado con nosotros.",
                            textoBoton = "Planear mi primer viaje",
                            onClickBoton = { navController.navigate("itinerario") }
                        )
                    } else {
                        ViajeCard(
                            titulo = "Riviera Maya Exclusive",
                            fecha = "10 Ene - 15 Ene 2025",
                            estado = "COMPLETADO",
                            colorEstado = Color.Gray,
                            onClickVoucher = { /* Lógica para ver detalles pasados */ }
                        )
                    }
                }
            }
        } // Fin del Column principal
    } // Fin del Scaffold
} // Fin de la función MisViajesScreen restaurado

@Composable
fun ViajeCard(titulo: String, fecha: String, estado: String, colorEstado: Color, onClickVoucher: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(estado, color = colorEstado, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Icon(Icons.Default.FlightTakeoff, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(titulo, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text(fecha, fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onClickVoucher,
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Ver Voucher", color = Color.White, fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = { /* Ver Itinerario */ },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Itinerario", color = BluePrimary, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String,
    subtitulo: String,
    textoBoton: String,
    onClickBoton: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color.White, shape = RoundedCornerShape(50.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(50.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = titulo,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitulo,
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onClickBoton,
            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(textoBoton, color = BluePrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}