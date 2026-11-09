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
                .widthIn(max = 450.dp) // Evita que se estire demasiado en tablets
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .imePadding(), // Sube el formulario automáticamente al abrir el teclado
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = logo_globaltours),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(160.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "Bienvenid@ a este viaje",
                color = BluePrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Tarjeta blanca para agrupar los campos de texto
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

                    if (mensajeError.isNotEmpty()) {
                        Text(
                            text = mensajeError,
                            color = Color(0xFFD32F2F),
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
}