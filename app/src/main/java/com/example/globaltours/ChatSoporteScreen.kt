package com.example.globaltours

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class MensajeChat(val texto: String, val esUsuario: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatSoporteScreen(navController: NavController) {
    var textoMensaje by remember { mutableStateOf("") }

    // Lista de mensajes (Simulando una base de datos)
    var mensajes by remember {
        mutableStateOf(
            listOf(
                MensajeChat("¡Hola, Juan! Soy tu Concierge de Global Tours. ¿En qué te puedo ayudar hoy?", esUsuario = false)
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Soporte VIP", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Agente en línea", color = GoldAccent, fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BluePrimary)
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textoMensaje,
                    onValueChange = { textoMensaje = it },
                    placeholder = { Text("Escribe un mensaje...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (textoMensaje.isNotBlank()) {
                            mensajes = mensajes + MensajeChat(textoMensaje, esUsuario = true)
                            textoMensaje = ""
                            // Aquí podrías agregar lógica para recibir una respuesta automática
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(GoldAccent, RoundedCornerShape(24.dp))
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Enviar", tint = BluePrimary)
                }
            }
        },
        containerColor = BackgroundBeige
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(mensajes) { mensaje ->
                BurbujaMensaje(mensaje)
            }
        }
    }
}

@Composable
fun BurbujaMensaje(mensaje: MensajeChat) {
    val alineacion = if (mensaje.esUsuario) Alignment.CenterEnd else Alignment.CenterStart
    val colorFondo = if (mensaje.esUsuario) BluePrimary else Color.White
    val colorTexto = if (mensaje.esUsuario) Color.White else TextDark
    val forma = if (mensaje.esUsuario) {
        RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alineacion
    ) {
        Card(
            shape = forma,
            colors = CardDefaults.cardColors(containerColor = colorFondo),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = mensaje.texto,
                color = colorTexto,
                fontSize = 15.sp,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}