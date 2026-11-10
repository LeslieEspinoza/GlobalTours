package com.example.globaltours

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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

data class MensajeChat(val texto: String, val esUsuario: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatSoporteIAScreen(navController: NavController) {
    var mensajeInput by remember { mutableStateOf("") }

    // Mensaje inicial con el menú de opciones numéricas
    val mensajeBienvenida = """
        ¡Hola! Soy tu asistente de viaje IA de GlobalTours ✈️. Puedes escribirme tu duda o elegir una de las siguientes opciones enviando el número:
        
        1️⃣ ¿Cómo cancelo una reserva?
        2️⃣ ¿Qué incluyen los paquetes VIP?
        3️⃣ Métodos de pago aceptados
        4️⃣ Hablar con un asesor humano
    """.trimIndent()

    var listaMensajes by remember { mutableStateOf(listOf(MensajeChat(mensajeBienvenida, false))) }
    var aiEscribiendo by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Opciones rápidas para que el usuario no tenga que teclear
    val opcionesRapidas = listOf("1", "2", "3", "4")

    // Función centralizada para enviar mensajes
    fun enviarMensaje(texto: String) {
        if (texto.isBlank()) return

        listaMensajes = listaMensajes + MensajeChat(texto, true)
        mensajeInput = ""
        aiEscribiendo = true

        scope.launch {
            delay(1200) // Simula tiempo de procesamiento de la IA
            val respuestaIA = generarRespuestaIA(texto)
            listaMensajes = listaMensajes + MensajeChat(respuestaIA, false)
            aiEscribiendo = false
        }
    }

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
                        Text(
                            "Escribiendo...",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            // Opciones de respuesta rápida y campo de texto
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {

                    // Chips de opciones rápidas (1, 2, 3, 4)
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, start = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(opcionesRapidas) { opcion ->
                            AssistChip(
                                onClick = { enviarMensaje(opcion) },
                                label = { Text("Opción $opcion") },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = BackgroundBeige,
                                    labelColor = BluePrimary
                                ),
                                border = AssistChipDefaults.assistChipBorder(
                                    borderColor = GoldAccent,
                                    enabled = true
                                )
                            )
                        }
                    }

                    // Input de Texto
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = mensajeInput,
                            onValueChange = { mensajeInput = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Escribe el número o tu duda...") },
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BluePrimary,
                                unfocusedBorderColor = Color.LightGray
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        IconButton(
                            onClick = { enviarMensaje(mensajeInput) },
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

// Función simuladora de IA actualizada para reconocer números y texto libre
fun generarRespuestaIA(pregunta: String): String {
    val q = pregunta.lowercase().trim()

    return when {
        // Opción 1: Cancelaciones
        q == "1" || q.contains("cancel") ->
            "Para cancelar una reserva, ve a la sección 'Mis Reservas', selecciona tu viaje y pulsa 'Cancelar'. Tienes hasta 48 horas antes del viaje para un reembolso completo según nuestras políticas."

        // Opción 2: Paquetes
        q == "2" || q.contains("incluye") || q.contains("paquete") ->
            "Nuestros paquetes VIP generalmente incluyen: \n• Vuelos ida y vuelta \n• Hospedaje 5 estrellas \n• Traslados aeropuerto-hotel \n• Desayunos buffet.\n¿Te interesa algún destino en particular?"

        // Opción 3: Pagos
        q == "3" || q.contains("pago") || q.contains("tarjeta") || q.contains("metodo") ->
            "Aceptamos pagos con tarjetas de crédito/débito (Visa, Mastercard, Amex). Todo el proceso de pago está encriptado y es 100% seguro."

        // Opción 4: Soporte Humano
        q == "4" || q.contains("contacto") || q.contains("humano") || q.contains("asesor") ->
            "Entendido. Para hablar con un asesor físico, te sugiero volver a la pantalla de Soporte y usar la opción 'Generar Ticket' o 'Agendar Cita'. Un experto se pondrá en contacto contigo pronto."

        // Saludos
        q.contains("hola") || q.contains("buenos") || q.contains("buenas") ->
            "¡Hola nuevamente! ¿En cuál de las opciones numéricas te puedo asistir o qué otra duda tienes?"

        // Fallback (IA Generativa simulada para texto no reconocido)
        else ->
            "Esa es una pregunta interesante. Como modelo de IA de GlobalTours, mi base de conocimientos sugiere que consultes directamente con un asesor para detalles tan específicos. ¿Deseas explorar la opción 4?"
    }
}