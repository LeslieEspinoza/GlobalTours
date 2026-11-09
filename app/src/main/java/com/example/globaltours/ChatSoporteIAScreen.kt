package com.example.globaltours

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.CircleShape


data class MensajeChat(val texto: String, val esUsuario: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatSoporteIAScreen(navController: NavController) {
    var mensajeInput by remember { mutableStateOf("") }
    var listaMensajes by remember { mutableStateOf(listOf(
        MensajeChat("¡Hola! Soy tu asistente de viaje IA de GlobalTours ✈️. ¿En qué te puedo ayudar hoy? (Ej. ¿Qué incluye el paquete a París?, ¿Cómo cancelo una reserva?)", false)
    )) }
    var aiEscribiendo by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SmartToy, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Asistente IA", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
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
        ) {
            // Lista de Mensajes
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listaMensajes) { mensaje ->
                    BurbujaMensaje(mensaje)
                }

                if (aiEscribiendo) {
                    item {
                        Text("Escribiendo...", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            // Input de Texto
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = mensajeInput,
                        onValueChange = { mensajeInput = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Escribe tu duda aquí...") },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(
                        onClick = {
                            if (mensajeInput.isNotBlank()) {
                                val textoUsuario = mensajeInput
                                listaMensajes = listaMensajes + MensajeChat(textoUsuario, true)
                                mensajeInput = ""
                                aiEscribiendo = true

                                // Simular respuesta de la IA
                                scope.launch {
                                    delay(1500) // Simula tiempo de procesamiento
                                    val respuestaIA = generarRespuestaIA(textoUsuario)
                                    listaMensajes = listaMensajes + MensajeChat(respuestaIA, false)
                                    aiEscribiendo = false
                                }
                            }
                        },
                        modifier = Modifier
                            .size(50.dp)
                            .background(BluePrimary, CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun BurbujaMensaje(mensaje: MensajeChat) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (mensaje.esUsuario) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .background(
                    color = if (mensaje.esUsuario) BluePrimary else Color.White,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (mensaje.esUsuario) 16.dp else 4.dp,
                        bottomEnd = if (mensaje.esUsuario) 4.dp else 16.dp
                    )
                )
                .padding(16.dp)
        ) {
            Text(
                text = mensaje.texto,
                color = if (mensaje.esUsuario) Color.White else Color.Black,
                fontSize = 15.sp
            )
        }
    }
}

// Función básica simuladora de IA (Aquí puedes conectar una API real luego)
fun generarRespuestaIA(pregunta: String): String {
    val q = pregunta.lowercase()
    return when {
        q.contains("cancel") -> "Para cancelar una reserva, por favor ve a la sección 'Mis Reservas', selecciona el viaje y pulsa 'Cancelar'. Revisa nuestras políticas de reembolso antes de hacerlo."
        q.contains("incluye") || q.contains("paquete") -> "Nuestros paquetes VIP generalmente incluyen vuelos ida y vuelta, hospedaje 5 estrellas, traslados y desayunos. Si quieres detalles de un paquete específico, ¡dime cuál!"
        q.contains("contacto") || q.contains("humano") || q.contains("asesor") -> "Si tu consulta es compleja, te sugiero generar un Ticket o Agendar una Cita desde la pantalla anterior para que un asesor físico te atienda."
        else -> "Esa es una gran pregunta. Como asistente IA, aún estoy aprendiendo. Si necesitas asistencia más profunda, te recomiendo generar un Ticket de Soporte en tu perfil."
    }
}