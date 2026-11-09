package com.example.globaltours

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
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
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class ClienteInsert(
    val auth_id: String,
    val nombres: String,
    val apellidos: String,
    val edad: Int,
    val telefono: String,
    val direccion: String,
    val pais: String,
    val email: String,
    val password: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(navController: NavController) {
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var pais by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }

    var expandirPais by remember { mutableStateOf(false) }
    val opcionesPaises = listOf("El Salvador", "Guatemala", "Honduras", "Costa Rica", "México", "Colombia", "Estados Unidos", "Otro")

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

    fun registrarCliente() {

        if (nombres.isBlank() || apellidos.isBlank() || emailInput.isBlank() || passwordInput.isBlank() || pais.isBlank()) {
            mensajeError = "Por favor, llena todos los campos obligatorios."
            return
        }
        val edadNum = edad.toIntOrNull()
        if (edadNum == null || edadNum < 18) {
            mensajeError = "Debes ser mayor de 18 años para registrarte."
            return
        }


        cargando = true
        mensajeError = ""

        scope.launch {
            try {
                val authResult = SupabaseProvider.client.auth.signUpWith(Email) {
                    email = emailInput
                    password = passwordInput
                }

                val usuarioId = SupabaseProvider.client.auth.currentUserOrNull()?.id
                    ?: throw Exception("No se pudo obtener el ID del usuario")

// Reemplazamos el mapOf por el data class
                val nuevoCliente = ClienteInsert(
                    auth_id = usuarioId,
                    nombres = nombres,
                    apellidos = apellidos,
                    edad = edadNum,
                    telefono = telefono,
                    direccion = direccion,
                    pais = pais,
                    email = emailInput,
                    password = passwordInput

                )

// Insertamos directamente el objeto
                SupabaseProvider.client.from("clientes").insert(nuevoCliente)

// Navegar a la pantalla principal y remover el registro de la pila
                navController.navigate("home_cliente") {
                    popUpTo("registro") { inclusive = true }
                }
            } catch (e: Exception) {
                android.util.Log.e("REGISTRO_ERROR", "Fallo: ${e.message}", e)
                mensajeError = "Error al registrar. Verifica si el correo ya existe."
            } finally {
                cargando = false
            }
        }
    }

    // Contenedor principal que centra el contenido en pantallas grandes
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBeige)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 500.dp) // Evita que se estire demasiado en tablets
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .imePadding(), // Empuja el contenido hacia arriba cuando sale el teclado
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.logo_globaltours),
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp)
            )

            Text(
                text = "Crear Cuenta",
                color = BluePrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(top = 16.dp)
            )

            Text(
                text = "Ingresa tus datos para comenzar tu viaje",
                color = Color.DarkGray,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = nombres, onValueChange = { nombres = it }, label = { Text("Nombres") },
                        leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                        colors = textFieldColors, shape = textFieldShape, modifier = Modifier.fillMaxWidth(), singleLine = true
                    )

                    OutlinedTextField(
                        value = apellidos, onValueChange = { apellidos = it }, label = { Text("Apellidos") },
                        leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                        colors = textFieldColors, shape = textFieldShape, modifier = Modifier.fillMaxWidth(), singleLine = true
                    )

                    OutlinedTextField(
                        value = emailInput, onValueChange = { emailInput = it }, label = { Text("Correo Electrónico") },
                        leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = textFieldColors, shape = textFieldShape, modifier = Modifier.fillMaxWidth(), singleLine = true
                    )

                    OutlinedTextField(
                        value = passwordInput, onValueChange = { passwordInput = it }, label = { Text("Contraseña (mín 8 car.)") },
                        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = textFieldColors, shape = textFieldShape, modifier = Modifier.fillMaxWidth(), singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp) // Reducimos el espacio entre ellos
                    ) {
                        OutlinedTextField(
                            value = edad,
                            onValueChange = { edad = it },
                            label = { Text("Edad") },
                            // Quitamos el ícono de edad para ahorrar espacio visual en pantallas pequeñas
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = textFieldColors,
                            shape = textFieldShape,
                            modifier = Modifier.weight(0.35f), // Le damos solo el 35% del ancho
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = telefono,
                            onValueChange = { telefono = it },
                            label = { Text("Teléfono") },
                            leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = textFieldColors,
                            shape = textFieldShape,
                            modifier = Modifier.weight(0.65f), // Le damos el 65% del ancho
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección (Opcional)") },
                        leadingIcon = { Icon(Icons.Outlined.Home, contentDescription = null) },
                        colors = textFieldColors, shape = textFieldShape, modifier = Modifier.fillMaxWidth()
                    )

                    ExposedDropdownMenuBox(
                        expanded = expandirPais,
                        onExpandedChange = { expandirPais = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = pais, onValueChange = {}, readOnly = true, label = { Text("País") },
                            leadingIcon = { Icon(Icons.Outlined.Public, contentDescription = null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirPais) },
                            colors = textFieldColors, shape = textFieldShape, modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandirPais,
                            onDismissRequest = { expandirPais = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            opcionesPaises.forEach { opcion ->
                                DropdownMenuItem(
                                    text = { Text(opcion) },
                                    onClick = {
                                        pais = opcion
                                        expandirPais = false
                                    }
                                )
                            }
                        }
                    }

                    if (mensajeError.isNotEmpty()) {
                        Text(text = mensajeError, color = Color(0xFFD32F2F), fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { registrarCliente() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                enabled = !cargando,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                if (cargando) {
                    CircularProgressIndicator(color = BluePrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Confirmar Registro", color = BluePrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            TextButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("¿Ya tienes cuenta? Inicia sesión", color = BluePrimary, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}