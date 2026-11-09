package com.example.globaltours

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasarelaPagoScreen(
    navController: NavController,
    reservaId: Long
) {
    val context = LocalContext.current
    var numeroTarjeta by remember { mutableStateOf("") }
    var nombreTitular by remember { mutableStateOf("") }
    var fechaExpiracion by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    var procesando by remember { mutableStateOf(false) }
    var mostrarDialogoExito by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    var mostrarDatePicker by remember { mutableStateOf(false) }
    var reserva by remember { mutableStateOf<ReservaModel?>(null) }

    val scope = rememberCoroutineScope()
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(reservaId) {
        try {
            val res = SupabaseProvider.client.from("reservas")
                .select {
                    filter {
                        eq("id", reservaId)
                    }
                }.decodeSingleOrNull<ReservaModel>()
            reserva = res
        } catch (e: Exception) {
            android.util.Log.e("PAGO_INIT_ERROR", "Error al cargar datos: ${e.message}", e)
        }
    }

    // 1. MODAL DE SELECCIÓN DE FECHA (DatePicker)
    if (mostrarDatePicker) {
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                                timeInMillis = millis
                            }
                            val mes = String.format("%02d", calendar.get(Calendar.MONTH) + 1)
                            val anio = calendar.get(Calendar.YEAR).toString().takeLast(2)
                            fechaExpiracion = "$mes/$anio"
                        }
                        mostrarDatePicker = false
                    }
                ) {
                    Text("Aceptar", fontWeight = FontWeight.Bold, color = BluePrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDatePicker = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // 2. DIÁLOGO DE CONFIRMACIÓN DE ÉXITO (PAGO EXITOSO)
    if (mostrarDialogoExito) {
        AlertDialog(
            onDismissRequest = { /* No cerrar al tocar afuera */ },
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(54.dp)
                )
            },
            title = {
                Text(
                    text = "¡Pago Procesado con Éxito!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = TextDark
                )
            },
            text = {
                Text(
                    text = "Tu pago ha sido registrado en el sistema. Puedes acceder a tu voucher oficial de abordaje o volver a tu historial de viajes.",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoExito = false
                        navController.navigate("voucher?id=$reservaId") {
                            popUpTo("mis_viajes") { inclusive = false }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Ver Voucher Ahora", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        mostrarDialogoExito = false
                        navController.navigate("mis_viajes") {
                            popUpTo("home_cliente") { inclusive = false }
                        }
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Ir a Mis Viajes", color = BluePrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // 3. DIÁLOGO DE ERROR (Si falla la conexión o Supabase)
    mensajeError?.let { errorText ->
        AlertDialog(
            onDismissRequest = { mensajeError = null },
            icon = { Icon(Icons.Default.Error, contentDescription = null, tint = Color.Red, modifier = Modifier.size(48.dp)) },
            title = { Text("Error al Procesar", fontWeight = FontWeight.Bold) },
            text = { Text(errorText, fontSize = 14.sp, color = Color.DarkGray) },
            confirmButton = {
                Button(
                    onClick = { mensajeError = null },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Text("Reintentar", color = Color.White)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pasarela de Pago Segura", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Color.White)
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Resumen de la Orden
            reserva?.let { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Resumen de tu Pedido", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(item.titulo, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Text(item.detalle, fontSize = 13.sp, color = Color.DarkGray)

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total a Liquidar:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            Text(
                                "$${item.precio.toInt()} USD",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = BluePrimary
                            )
                        }
                    }
                }
            }

            // Formulario de Tarjeta
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CreditCard, contentDescription = null, tint = BluePrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ingresa los Datos de la Tarjeta", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    }

                    // Formateo de tarjeta 4 en 4
                    OutlinedTextField(
                        value = numeroTarjeta,
                        onValueChange = { input ->
                            val soloDigitos = input.filter { it.isDigit() }.take(16)
                            numeroTarjeta = soloDigitos.chunked(4).joinToString(" ")
                        },
                        label = { Text("Número de Tarjeta") },
                        placeholder = { Text("1234 5678 9012 3456") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        trailingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray)
                        }
                    )

                    OutlinedTextField(
                        value = nombreTitular,
                        onValueChange = { nombreTitular = it },
                        label = { Text("Nombre del Titular") },
                        placeholder = { Text("Como figura en el plástico") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = fechaExpiracion,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Expiración") },
                                placeholder = { Text("MM/AA") },
                                trailingIcon = {
                                    Icon(Icons.Default.CalendarToday, contentDescription = "Elegir Fecha", tint = BluePrimary)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { mostrarDatePicker = true }
                            )
                        }

                        OutlinedTextField(
                            value = cvv,
                            onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) cvv = it },
                            label = { Text("CVV") },
                            placeholder = { Text("123") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val digitosLimpios = numeroTarjeta.filter { it.isDigit() }
                    val datosValidos = digitosLimpios.length == 16 && nombreTitular.isNotBlank() && fechaExpiracion.isNotBlank() && cvv.length >= 3

                    Button(
                        onClick = {
                            if (datosValidos) {
                                procesando = true
                                mensajeError = null
                                scope.launch {
                                    try {
                                        // A) Actualizar la reserva a "Confirmado"
                                        SupabaseProvider.client.from("reservas").update({
                                            set("estado", "Confirmado")
                                        }) {
                                            filter {
                                                eq("id", reservaId)
                                            }
                                        }

                                        // B) Intentar crear la notificación en segundo plano
                                        try {
                                            val userId = SupabaseProvider.client.auth.currentUserOrNull()?.id
                                            if (userId != null && reserva != null) {
                                                val notif = NotificacionModel(
                                                    auth_id = userId,
                                                    titulo = "¡Pago Confirmado!",
                                                    mensaje = "Tu pago de $${reserva!!.precio.toInt()} USD para '${reserva!!.titulo}' fue recibido con éxito.",
                                                    tipo = "reserva"
                                                )
                                                SupabaseProvider.client.from("notifications").insert(notif)
                                            }
                                        } catch (eNotif: Exception) {
                                            android.util.Log.w("NOTIF_WARNING", "No se pudo insertar la notificación: ${eNotif.message}")
                                        }

                                        // C) Toast de respaldo rápido e indicación de diálogo de éxito
                                        Toast.makeText(context, "¡Pago registrado correctamente!", Toast.LENGTH_SHORT).show()
                                        mostrarDialogoExito = true

                                    } catch (e: Exception) {
                                        android.util.Log.e("PROCESAR_PAGO_ERROR", "Error al pagar: ${e.message}", e)
                                        mensajeError = "No se pudo completar el pago: ${e.localizedMessage ?: e.message}"
                                    } finally {
                                        procesando = false
                                    }
                                }
                            }
                        },
                        enabled = !procesando && datosValidos,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (procesando) {
                            CircularProgressIndicator(color = BluePrimary, modifier = Modifier.size(24.dp))
                        } else {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = BluePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pagar y Confirmar Reserva", color = BluePrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
}