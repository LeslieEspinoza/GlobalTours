package com.example.globaltours
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue // Importación clave para el 'by remember'
import androidx.compose.runtime.setValue // Importación clave para el 'by remember'
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

// Paleta de colores basada en los diseños de GlobalTours
val BluePrimary = Color(0xFF0F2D40)
val GoldAccent = Color(0xFFF2BA50)
val BackgroundBeige = Color(0xFFF4F0E6)
val TextDark = Color(0xFF2A2A2A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    // 1. Renombramos las variables para evitar conflictos con Supabase
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var cargando by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    fun iniciarSesion() {
        if (emailInput.isBlank() || passwordInput.isBlank()) {
            mensajeError = "Por favor, completa todos los campos"
            return
        }

        cargando = true
        mensajeError = ""

        scope.launch {
            try {
                // 2. Usamos las variables renombradas directamente
                SupabaseProvider.client.auth.signInWith(Email) {
                    email = emailInput
                    password = passwordInput
                }

                navController.navigate("home_cliente") {
                    popUpTo("login") { inclusive = true }
                }

            } catch (e: Exception) {
                mensajeError = "Credenciales incorrectas o error de conexión."
                e.printStackTrace()
            } finally {
                cargando = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBeige)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = logo_globaltours),
            contentDescription = "Logo",
            modifier = Modifier
                .size(180.dp)
                .padding(bottom = 24.dp)
        )

        Text(
            text = "Bienvenid@ a este viaje!",
            color = BluePrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        val textFieldColors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = BluePrimary,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = BluePrimary
        )

        OutlinedTextField(
            value = emailInput,
            onValueChange = { emailInput = it },
            label = { Text("Correo electrónico", color = Color.Gray) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = textFieldColors,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = passwordInput,
            onValueChange = { passwordInput = it },
            label = { Text("Contraseña", color = Color.Gray) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = textFieldColors,
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = null, tint = BluePrimary)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            singleLine = true
        )

        if (mensajeError.isNotEmpty()) {
            Text(
                text = mensajeError,
                color = Color(0xFFD32F2F),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(24.dp))
        }

        Button(
            onClick = { iniciarSesion() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
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