package com.example.globaltours

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilClienteScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var cargando by remember { mutableStateOf(true) }
    var guardando by remember { mutableStateOf(false) }
    var mostrarModalCerrarSesion by remember { mutableStateOf(false) }
    var modoEdicion by remember { mutableStateOf(false) }

    // Campos del perfil actualizados
    var authId by remember { mutableStateOf("") }
    var emailUser by remember { mutableStateOf("") }
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var pasaporte by remember { mutableStateOf("") }

    // ⚠️ NUEVO: Estado para saber si tiene más de 3 reservas
    var esViajeroFrecuente by remember { mutableStateOf(false) }

    // Cargar datos del usuario desde Supabase
    LaunchedEffect(Unit) {
        try {
            val user = SupabaseProvider.client.auth.currentUserOrNull()
            if (user != null) {
                authId = user.id
                emailUser = user.email ?: ""

                // 1. Obtener los datos personales
                val cliente = SupabaseProvider.client.from("clientes")
                    .select {
                        filter {
                            eq("auth_id", authId)
                        }
                    }.decodeSingleOrNull<ClienteModel>()

                if (cliente != null) {
                    nombres = cliente.nombres ?: ""
                    apellidos = cliente.apellidos ?: ""
                    telefono = cliente.telefono ?: ""
                    pasaporte = cliente.pasaporte ?: ""
                } else {
                    nombres = user.userMetadata?.get("nombre")?.toString()?.replace("\"", "") ?: ""
                }

                // 2. ⚠️ NUEVO: Contar la cantidad de reservas para el estatus de viajero frecuente
                val cantidadReservas = SupabaseProvider.client.from("reservas")
                    .select {
                        filter {
                            eq("auth_id", authId)
                        }
                    }.decodeList<ReservaModel>().size

                // Se activa si tiene más de 3 reservas (puedes cambiarlo a >= 3 si lo prefieres)
                esViajeroFrecuente = cantidadReservas > 3
            }
        } catch (e: Exception) {
            android.util.Log.e("PERFIL_ERROR", "Error al cargar perfil: ${e.message}", e)
        } finally {
            cargando = false
        }
    }

    if (mostrarModalCerrarSesion) {
        AlertDialog(
            onDismissRequest = { mostrarModalCerrarSesion = false },
            icon = { Icon(Icons.Default.Logout, contentDescription = null, tint = Color.Red, modifier = Modifier.size(36.dp)) },
            title = { Text("¿Cerrar Sesión?", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas salir de tu cuenta de GlobalTours?") },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarModalCerrarSesion = false
                        scope.launch {
                            try {
                                SupabaseProvider.client.auth.signOut()
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error al cerrar sesión: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Cerrar Sesión", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { mostrarModalCerrarSesion = false }) {
                    Text("Cancelar", color = Color.Black)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { modoEdicion = !modoEdicion }) {
                        Icon(
                            imageVector = if (modoEdicion) Icons.Default.Close else Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BluePrimary)
            )
        },
        bottomBar = {
            NavegacionInferiorCliente(navController = navController, rutaActual = "perfil")
        },
        containerColor = BackgroundBeige
    ) { paddingValues ->
        if (cargando) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BluePrimary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // --- SECCIÓN: Avatar y Nombre ---
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(BluePrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(54.dp)
                    )
                }

                val nombreCompleto = if (nombres.isNotBlank() || apellidos.isNotBlank()) {
                    "$nombres $apellidos".trim()
                } else {
                    "Nuevo Viajero"
                }

                Text(
                    text = nombreCompleto,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // ⚠️ NUEVO: Solo mostramos la insignia si cumple la condición
                if (esViajeroFrecuente) {
                    Surface(
                        color = GoldAccent.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Miembro Frequent Flyer", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // --- SECCIÓN: Información de la Cuenta ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text("Información Personal", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BluePrimary)

                        OutlinedTextField(
                            value = emailUser,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            label = { Text("Correo Electrónico") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = nombres,
                            onValueChange = { nombres = it },
                            readOnly = !modoEdicion,
                            enabled = modoEdicion,
                            label = { Text("Nombres") },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = BluePrimary) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = apellidos,
                            onValueChange = { apellidos = it },
                            readOnly = !modoEdicion,
                            enabled = modoEdicion,
                            label = { Text("Apellidos") },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = BluePrimary) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = telefono,
                            onValueChange = { telefono = it },
                            readOnly = !modoEdicion,
                            enabled = modoEdicion,
                            label = { Text("Teléfono de Contacto") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = BluePrimary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = pasaporte,
                            onValueChange = { pasaporte = it },
                            readOnly = !modoEdicion,
                            enabled = modoEdicion,
                            label = { Text("Número de Pasaporte / ID") },
                            leadingIcon = { Icon(Icons.Default.FlightTakeoff, contentDescription = null, tint = BluePrimary) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        if (modoEdicion) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    guardando = true
                                    scope.launch {
                                        try {
                                            SupabaseProvider.client.from("clientes").upsert(
                                                mapOf(
                                                    "auth_id" to authId,
                                                    "email" to emailUser,
                                                    "nombres" to nombres,
                                                    "apellidos" to apellidos,
                                                    "telefono" to telefono,
                                                    "pasaporte" to pasaporte
                                                )
                                            )
                                            Toast.makeText(context, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
                                            modoEdicion = false
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Error al actualizar perfil", Toast.LENGTH_SHORT).show()
                                        } finally {
                                            guardando = false
                                        }
                                    }
                                },
                                enabled = !guardando,
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                if (guardando) {
                                    CircularProgressIndicator(color = BluePrimary, modifier = Modifier.size(20.dp))
                                } else {
                                    Icon(Icons.Default.Save, contentDescription = null, tint = BluePrimary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Guardar Cambios", color = BluePrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // --- SECCIÓN: Soporte Técnico ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Centro de Soporte", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                        Text("¿Necesitas ayuda con tu viaje? Nuestro equipo y asistente virtual están disponibles.", fontSize = 13.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = { navController.navigate("chat_soporte_ia") },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Chatbot de Asistencia IA (24/7)", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { navController.navigate("agendar_cita_soporte") },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BluePrimary)
                        ) {
                            Icon(Icons.Default.Event, contentDescription = null, tint = BluePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generar Ticket o Agendar Cita", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- SECCIÓN: Cerrar Sesión ---
                OutlinedButton(
                    onClick = { mostrarModalCerrarSesion = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cerrar Sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}