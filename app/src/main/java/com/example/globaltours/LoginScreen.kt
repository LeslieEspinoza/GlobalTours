package com.example.globaltours

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.globaltours.R.drawable.logo_globaltours
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.OTP
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.OtpType
import kotlinx.coroutines.launch

// Paleta de colores corporativa
val BluePrimary = Color(0xFF0F2D40)
val GoldAccent = Color(0xFFF2BA50)
val BackgroundBeige = Color(0xFFF4F0E6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var cargando by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Estados para el diálogo de recuperación con código (OTP)
    var mostrarDialogoRecuperar by remember { mutableStateOf(false) }
    var pasoRecuperacion by remember { mutableStateOf(1) } // 1: Pedir correo, 2: Pedir código y nueva clave
    var emailRecuperar by remember { mutableStateOf("") }
    var codigoRecuperacion by remember { mutableStateOf("") }
    var nuevaPassword by remember { mutableStateOf("") }
    var mensajeRecuperacion by remember { mutableStateOf("") }
    var procesandoRecuperacion by remember { mutableStateOf(false) }

    val textFieldShape = RoundedCornerShape(12.dp)
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedBorderColor = BluePrimary,
        unfocusedBorderColor = Color.LightGray,
        focusedLabelColor = BluePrimary,
        cursorColor = BluePrimary,
        focusedLeadingIconColor = BluePrimary,
        unfocusedLeadingIconColor = Color.Gray
    )

    fun iniciarSesion() {
        if (emailInput.isBlank() || passwordInput.isBlank()) {
            mensajeError = "Por favor, completa todos los campos"
            return
        }

        cargando = true
        mensajeError = ""

        scope.launch {
            try {
                SupabaseProvider.client.auth.signInWith(Email) {
                    email = emailInput
                    password = passwordInput
                }

                navController.navigate("home_cliente") {
                    popUpTo("login") { inclusive = true }
                }

            } catch (e: Exception) {
                mensajeError = "Credenciales incorrectas o error de conexión."
                android.util.Log.e("ERROR_SUPABASE", "Falló el login: ${e.message}", e)
            } finally {
                cargando = false
            }
        }
    }

    // Funciones de Recuperación
    fun enviarCorreoCodigo() {
        if (emailRecuperar.isBlank()) {
            mensajeRecuperacion = "Ingresa tu correo electrónico."
            return
        }
        procesandoRecuperacion = true
        mensajeRecuperacion = ""

        scope.launch {
            try {
                // Solicitar código a Supabase
                SupabaseProvider.client.auth.resetPasswordForEmail(emailRecuperar)
                pasoRecuperacion = 2
                mensajeRecuperacion = "Código enviado. Revisa tu correo."
            } catch (e: Exception) {
                mensajeRecuperacion = "Error al enviar código: ${e.message}"
            } finally {
                procesandoRecuperacion = false
            }
        }
    }

    fun cambiarPasswordConCodigo() {
        if (codigoRecuperacion.isBlank() || nuevaPassword.isBlank()) {
            mensajeRecuperacion = "Ingresa el código y la nueva contraseña."
            return
        }
        procesandoRecuperacion = true
        mensajeRecuperacion = ""

        scope.launch {
            try {
                // 1. Verificar el OTP, esto inicia sesión temporalmente al usuario
                SupabaseProvider.client.auth.verifyEmailOtp(
                    type = OtpType.Email.RECOVERY,
                    email = emailRecuperar,
                    token = codigoRecuperacion
                )

                // 2. Actualizar a la nueva contraseña
                SupabaseProvider.client.auth.updateUser {
                    password = nuevaPassword
                }

                // 3. Cerrar sesión por seguridad y mandar al usuario a que inicie con la nueva clave
                SupabaseProvider.client.auth.signOut()

                mostrarDialogoRecuperar = false
                mensajeError = "¡Contraseña actualizada! Inicia sesión."
            } catch (e: Exception) {
                mensajeRecuperacion = "Código incorrecto o expirado."
                android.util.Log.e("ERROR_OTP", "Error cambiando clave: ${e.message}")
            } finally {
                procesandoRecuperacion = false
            }
        }
    }

    // Contenedor principal que centra el contenido
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBeige)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 450.dp)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = logo_globaltours),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(180.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "Bienvenid@ a este viaje",
                color = BluePrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Correo electrónico") },
                        leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = textFieldColors,
                        shape = textFieldShape,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Column {
                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text("Contraseña") },
                            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = textFieldColors,
                            shape = textFieldShape,
                            trailingIcon = {
                                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = image, contentDescription = "Mostrar/Ocultar", tint = Color.Gray)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        TextButton(
                            onClick = {
                                pasoRecuperacion = 1
                                emailRecuperar = emailInput
                                codigoRecuperacion = ""
                                nuevaPassword = ""
                                mensajeRecuperacion = ""
                                mostrarDialogoRecuperar = true
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("¿Olvidaste tu contraseña?", color = BluePrimary, fontSize = 12.sp)
                        }
                    }

                    if (mensajeError.isNotEmpty()) {
                        Text(
                            text = mensajeError,
                            color = if (mensajeError.contains("actualizada")) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { iniciarSesion() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                enabled = !cargando
            ) {
                if (cargando) {
                    CircularProgressIndicator(color = BluePrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Iniciar Sesión", color = BluePrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigate("registro") }) {
                Text("¿Eres nuevo? Regístrate aquí", color = BluePrimary, fontWeight = FontWeight.SemiBold)
            }
        }
    }

    // Diálogo de recuperación con 2 pasos
    if (mostrarDialogoRecuperar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoRecuperar = false },
            title = { Text("Recuperar contraseña", color = BluePrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    if (pasoRecuperacion == 1) {
                        Text("Ingresa el correo asociado a tu cuenta para enviarte un código de 6 dígitos.", fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = emailRecuperar,
                            onValueChange = { emailRecuperar = it },
                            label = { Text("Correo electrónico") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = textFieldColors,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    } else {
                        Text("Ingresa el código que enviamos a tu correo y tu nueva contraseña.", fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = codigoRecuperacion,
                            onValueChange = { codigoRecuperacion = it },
                            label = { Text("Código de 6 dígitos") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = textFieldColors,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = nuevaPassword,
                            onValueChange = { nuevaPassword = it },
                            label = { Text("Nueva contraseña") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            visualTransformation = PasswordVisualTransformation(),
                            colors = textFieldColors,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    if (mensajeRecuperacion.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = mensajeRecuperacion,
                            color = if (mensajeRecuperacion.contains("enviado")) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pasoRecuperacion == 1) enviarCorreoCodigo()
                        else cambiarPasswordConCodigo()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    enabled = !procesandoRecuperacion
                ) {
                    if (procesandoRecuperacion) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text(if (pasoRecuperacion == 1) "Enviar código" else "Cambiar contraseña")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoRecuperar = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            },
            containerColor = Color.White
        )
    }
}