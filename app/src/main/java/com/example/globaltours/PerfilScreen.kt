package com.example.globaltours

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var guardando by remember { mutableStateOf(false) }

    // Cargar datos actuales de Supabase al entrar
    LaunchedEffect(Unit) {
        val usuario = SupabaseProvider.client.auth.currentUserOrNull()
        email = usuario?.email ?: ""
        nombre = usuario?.userMetadata?.get("full_name")?.toString()?.replace("\"", "") ?: ""
        telefono = usuario?.userMetadata?.get("phone")?.toString()?.replace("\"", "") ?: ""
    }

    fun guardarPerfil() {
        guardando = true
        scope.launch {
            try {
                // Actualizar los metadatos del usuario en Supabase Auth
                SupabaseProvider.client.auth.updateUser {
                    data = buildJsonObject {
                        put("full_name", nombre)
                        put("phone", telefono)
                    }
                }
                Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                guardando = false
            }
        }
    }

    fun cerrarSesion() {
        scope.launch {
            SupabaseProvider.client.auth.signOut()
            navController.navigate("login") {
                popUpTo(0) { inclusive = true } // Limpiar el historial de navegación
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BluePrimary)
            )
        },
        bottomBar = { NavegacionInferiorCliente(navController, "perfil") }, // Asumiendo que agregas "perfil" a tu barra inferior
        containerColor = BackgroundBeige
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(BluePrimary.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(50.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Campos de texto
            OutlinedTextField(
                value = email,
                onValueChange = {},
                label = { Text("Correo Electrónico (No editable)") },
                enabled = false, // El correo no se edita aquí por seguridad
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(disabledTextColor = Color.DarkGray)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { guardarPerfil() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                enabled = !guardando
            ) {
                Text(if (guardando) "Guardando..." else "Guardar Cambios", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { cerrarSesion() }) {
                Text("Cerrar Sesión", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
            }
        }
    }
}