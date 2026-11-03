package com.example.globaltours

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(navController: NavController) {
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var pais by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var cargando by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        focusedBorderColor = BluePrimary,
        unfocusedBorderColor = Color.Transparent,
        cursorColor = BluePrimary
    )

    fun registrarCliente() {
        val edadNum = edad.toIntOrNull()
        if (edadNum == null || edadNum < 18) {
            mensajeError = "Debes ser mayor de 18 años"
            return
        }
        if (nombres.isBlank() || email.isBlank() || password.isBlank()) {
            mensajeError = "Llena todos los campos obligatorios"
            return
        }

        cargando = true
        mensajeError = ""

        val nuevoCliente = Cliente(
            nombres = nombres,
            apellidos = apellidos,
            edad = edadNum,
            telefono = telefono,
            direccion = direccion,
            pais = pais,
            email = email,
            password = password
        )

        scope.launch {
            try {
                SupabaseProvider.client.from("clientes").insert(nuevoCliente)
                // Retorna al login tras registro exitoso
                navController.popBackStack()
            } catch (e: Exception) {
                mensajeError = "Error al registrar: ${e.localizedMessage}"
            } finally {
                cargando = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBeige)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_globaltours),
            contentDescription = "Logo",
            modifier = Modifier
                .size(180.dp)
                .padding(bottom = 24.dp)
        )
        Text(
            text = "Crear Cuenta",
            color = BluePrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
        )

        OutlinedTextField(value = nombres, onValueChange = { nombres = it }, label = { Text("Nombres") }, colors = textFieldColors, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
        OutlinedTextField(value = apellidos, onValueChange = { apellidos = it }, label = { Text("Apellidos") }, colors = textFieldColors, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo Electrónico") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), colors = textFieldColors, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), colors = textFieldColors, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))

        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = edad, onValueChange = { edad = it }, label = { Text("Edad") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = textFieldColors, modifier = Modifier.weight(1f))
            OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), colors = textFieldColors, modifier = Modifier.weight(1.5f))
        }

        OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, colors = textFieldColors, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
        OutlinedTextField(value = pais, onValueChange = { pais = it }, label = { Text("País") }, colors = textFieldColors, modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp))

        if (mensajeError.isNotEmpty()) {
            Text(text = mensajeError, color = Color(0xFFD32F2F), modifier = Modifier.padding(bottom = 16.dp))
        }

        Button(
            onClick = { registrarCliente() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
            enabled = !cargando
        ) {
            if (cargando) CircularProgressIndicator(color = BluePrimary, modifier = Modifier.size(24.dp))
            else Text("Confirmar Registro", color = BluePrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        TextButton(onClick = { navController.popBackStack() }, modifier = Modifier.padding(top = 8.dp)) {
            Text("Volver al Login", color = BluePrimary)
        }
    }
}