package com.example.globaltours

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenciasScreen(navController: NavController) {
    var asiento by remember { mutableStateOf("Pasillo") }
    var dieta by remember { mutableStateOf("Ninguna") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preferencias de Viaje", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Sección Vuelo
            Text("Preferencia de Asiento", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OpcionBoton(texto = "Ventana", seleccionado = asiento == "Ventana", onClick = { asiento = "Ventana" }, modifier = Modifier.weight(1f))
                OpcionBoton(texto = "Pasillo", seleccionado = asiento == "Pasillo", onClick = { asiento = "Pasillo" }, modifier = Modifier.weight(1f))
            }

            // Sección Dieta
            Text("Requisitos Dietéticos", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                val opcionesDieta = listOf("Ninguna", "Vegetariana", "Vegana", "Sin Gluten")
                opcionesDieta.forEach { opcion ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        RadioButton(
                            selected = dieta == opcion,
                            onClick = { dieta = opcion },
                            colors = RadioButtonDefaults.colors(selectedColor = BluePrimary)
                        )
                        Text(opcion, color = Color.Black, fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar Preferencias", color = BluePrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun OpcionBoton(texto: String, seleccionado: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (seleccionado) BluePrimary else Color.Transparent,
            contentColor = if (seleccionado) Color.White else BluePrimary
        )
    ) {
        Text(texto, fontWeight = FontWeight.Bold)
    }
}