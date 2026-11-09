package com.example.globaltours

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.SupportAgent
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
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendarCitaScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var asunto by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var tipoSeleccionado by remember { mutableStateOf("Ticket Técnico") }
    val opcionesTipo = listOf("Ticket Técnico", "Cita Presencial (Asesor)", "Duda de Facturación")

    var expandirDropdown by remember { mutableStateOf(false) }
    var enviando by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Generar Solicitud", color = Color.White, fontWeight = FontWeight.Bold) },
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "¿En qué podemos ayudarte?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = BluePrimary
            )

            Text(
                "Completa este formulario y un asesor se pondrá en contacto contigo a la brevedad.",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Selector de Tipo
            ExposedDropdownMenuBox(
                expanded = expandirDropdown,
                onExpandedChange = { expandirDropdown = it }
            ) {
                OutlinedTextField(
                    value = tipoSeleccionado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de Solicitud") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirDropdown) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expandirDropdown,
                    onDismissRequest = { expandirDropdown = false }
                ) {
                    opcionesTipo.forEach { seleccion ->
                        DropdownMenuItem(
                            text = { Text(seleccion) },
                            onClick = {
                                tipoSeleccionado = seleccion
                                expandirDropdown = false
                            }
                        )
                    }
                }
            }

            // Asunto
            OutlinedTextField(
                value = asunto,
                onValueChange = { asunto = it },
                label = { Text("Asunto Breve") },
                leadingIcon = { Icon(Icons.Default.SupportAgent, contentDescription = null, tint = BluePrimary) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Descripción Detallada
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Describe tu situación") },
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = BluePrimary) },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botón Enviar
            Button(
                onClick = {
                    if (asunto.isBlank() || descripcion.isBlank()) {
                        Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    enviando = true
                    scope.launch {
                        try {
                            val userId = SupabaseProvider.client.auth.currentUserOrNull()?.id
                            if (userId != null) {
                                val nuevoTicket = TicketSoporteModel(
                                    auth_id = userId,
                                    tipo = tipoSeleccionado,
                                    asunto = asunto,
                                    descripcion = descripcion
                                )
                                SupabaseProvider.client.from("tickets_soporte").insert(nuevoTicket)

                                Toast.makeText(context, "Solicitud enviada con éxito", Toast.LENGTH_LONG).show()
                                navController.popBackStack() // Volver al perfil
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally {
                            enviando = false
                        }
                    }
                },
                enabled = !enviando,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (enviando) {
                    CircularProgressIndicator(color = BluePrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Enviar Solicitud", color = BluePrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}