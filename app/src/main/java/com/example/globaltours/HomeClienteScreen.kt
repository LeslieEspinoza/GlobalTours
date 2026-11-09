package com.example.globaltours

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

// --- MODELOS DE DATOS ---



// --- COMPOSABLE PRINCIPAL ---

@Composable
fun HomeClienteScreen(navController: NavController) {
    var nombreCliente by remember { mutableStateOf("Viajero") }
    var cargandoCliente by remember { mutableStateOf(true) }
    var listaPaquetes by remember { mutableStateOf<List<PaqueteTuristico>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            val userId = SupabaseProvider.client.auth.currentUserOrNull()?.id
            if (userId != null) {
                val cliente = SupabaseProvider.client.from("clientes")
                    .select {
                        filter {
                            eq("auth_id", userId)
                        }
                    }.decodeSingleOrNull<ClienteProfile>()

                if (cliente != null && cliente.nombres.isNotBlank()) {
                    nombreCliente = cliente.nombres.split(" ").firstOrNull() ?: cliente.nombres
                }
            }

            val paquetes = SupabaseProvider.client.from("paquetes_turisticos")
                .select {
                    filter {
                        eq("destacado", true)
                    }
                }.decodeList<PaqueteTuristico>()

            listaPaquetes = paquetes

        } catch (e: Exception) {
            android.util.Log.e("HOME_SUPABASE", "Error al cargar datos: ${e.message}", e)
        } finally {
            cargandoCliente = false
        }
    }

    Scaffold(
        bottomBar = { NavegacionInferiorCliente(navController, "home_cliente") },
        containerColor = BackgroundBeige
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            HeaderSection(navController = navController, nombreCliente = nombreCliente, cargando = cargandoCliente)

            PaquetesDestacadosSection(paquetes = listaPaquetes, navController = navController)

            MotorReservasSection(navController = navController)

            IAPlannerSection(navController = navController)
        }
    }
}

// --- SECCIONES DE LA INTERFAZ ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderSection(navController: NavController, nombreCliente: String, cargando: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BluePrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (cargando) {
                    CircularProgressIndicator(color = GoldAccent, modifier = Modifier.size(20.dp))
                } else {
                    Text(
                        text = "¡Hola, $nombreCliente! 👋",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "¿A dónde quieres viajar hoy?",
                    color = GoldAccent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            IconButton(
                onClick = { navController.navigate("notificaciones") },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
            ) {
                BadgedBox(
                    badge = { Badge(containerColor = Color(0xFFE53935)) { Text("!", color = Color.White) } }
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notificaciones", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun PaquetesDestacadosSection(paquetes: List<PaqueteTuristico>, navController: NavController) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Destinos Destacados",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            TextButton(onClick = { navController.navigate("reservas") }) {
                Text("Ver todos", color = BluePrimary, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (paquetes.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("Cargando ofertas...", color = Color.Gray, fontSize = 14.sp)
                }
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(paquetes) { paquete ->
                    TarjertaPaqueteItem(paquete,
                        onClick = {
                            navController.navigate("detalle_paquete?id=${paquete.id}")
                        })
                }
                }
            }
        }
    }


@Composable
fun TarjertaPaqueteItem(paquete: PaqueteTuristico, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = paquete.imagen_url,
                contentDescription = paquete.titulo,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = GoldAccent.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${paquete.duracion_dias} Días",
                        color = BluePrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Icon(Icons.Outlined.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = paquete.titulo, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
            Text(text = paquete.destino, color = Color.Gray, fontSize = 13.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Desde", fontSize = 10.sp, color = Color.Gray)
                    Text("$${paquete.precio_base.toInt()} USD", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = BluePrimary)
                }

                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Detalles", color = BluePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- MOTOR DE RESERVAS CON ESTADO REACTIVO Y BÚSQUEDA ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotorReservasSection(navController: NavController) {
    var tipoBusqueda by remember { mutableStateOf("Vuelos") }

    // Opciones cargadas dinámicamente o predefinidas para la selección
    val origenesVuelo = listOf("San Salvador (SAL)", "Guatemala (GUA)", "México (MEX)")
    val destinosVuelo = listOf("Madrid (MAD)", "Cancún (CUN)", "Tokio (TKO)")
    val ciudadesHoteles = listOf("Madrid (MAD)", "Cancún (CUN)", "Tokio (TKO)")

    var origenSeleccionado by remember { mutableStateOf(origenesVuelo.first()) }
    var destinoSeleccionado by remember { mutableStateOf(destinosVuelo.first()) }
    var ciudadHotelSeleccionada by remember { mutableStateOf(ciudadesHoteles.first()) }
    var ciudadExcursionSeleccionada by remember { mutableStateOf(ciudadesHoteles.first()) }

    Column {
        Text(
            text = "Motor de Busqueda y Disponibilidad",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = tipoBusqueda == "Vuelos",
                onClick = { tipoBusqueda = "Vuelos" },
                label = { Text("Vuelos") },
                leadingIcon = { Icon(Icons.Default.Flight, null, modifier = Modifier.size(16.dp)) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BluePrimary, selectedLabelColor = Color.White, selectedLeadingIconColor = Color.White)
            )
            FilterChip(
                selected = tipoBusqueda == "Hoteles",
                onClick = { tipoBusqueda = "Hoteles" },
                label = { Text("Hoteles") },
                leadingIcon = { Icon(Icons.Default.Hotel, null, modifier = Modifier.size(16.dp)) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BluePrimary, selectedLabelColor = Color.White, selectedLeadingIconColor = Color.White)
            )
            FilterChip(
                selected = tipoBusqueda == "Excursiones",
                onClick = { tipoBusqueda = "Excursiones" },
                label = { Text("Excursiones") },
                leadingIcon = { Icon(Icons.Default.Map, null, modifier = Modifier.size(16.dp)) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BluePrimary, selectedLabelColor = Color.White, selectedLeadingIconColor = Color.White)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                when (tipoBusqueda) {
                    "Vuelos" -> FormularioVuelos(
                        origenes = origenesVuelo, origen = origenSeleccionado, onOrigenChange = { origenSeleccionado = it },
                        destinos = destinosVuelo, destino = destinoSeleccionado, onDestinoChange = { destinoSeleccionado = it }
                    )
                    "Hoteles" -> FormularioHoteles(
                        ciudades = ciudadesHoteles, ciudad = ciudadHotelSeleccionada, onCiudadChange = { ciudadHotelSeleccionada = it }
                    )
                    "Excursiones" -> FormularioExcursiones(
                        ciudades = ciudadesHoteles, ciudad = ciudadExcursionSeleccionada, onCiudadChange = { ciudadExcursionSeleccionada = it }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val ruta = when (tipoBusqueda) {
                            "Vuelos" -> "resultados_busqueda?tipo=Vuelos&origen=$origenSeleccionado&destino=$destinoSeleccionado"
                            "Hoteles" -> "resultados_busqueda?tipo=Hoteles&destino=$ciudadHotelSeleccionada"
                            else -> "resultados_busqueda?tipo=Excursiones&destino=$ciudadExcursionSeleccionada"
                        }
                        navController.navigate(ruta)
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Consultar Disponibilidad", color = BluePrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- SUB-FORMULARIOS CON DROPDOWNS ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioVuelos(
    origenes: List<String>, origen: String, onOrigenChange: (String) -> Unit,
    destinos: List<String>, destino: String, onDestinoChange: (String) -> Unit
) {
    DropdownSelector(label = "Origen", opciones = origenes, seleccion = origen, onSeleccion = onOrigenChange, icon = Icons.Default.FlightTakeoff)
    Spacer(modifier = Modifier.height(8.dp))
    DropdownSelector(label = "Destino", opciones = destinos, seleccion = destino, onSeleccion = onDestinoChange, icon = Icons.Default.FlightLand)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioHoteles(
    ciudades: List<String>, ciudad: String, onCiudadChange: (String) -> Unit
) {
    DropdownSelector(label = "Ciudad / Destino", opciones = ciudades, seleccion = ciudad, onSeleccion = onCiudadChange, icon = Icons.Default.LocationOn)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioExcursiones(
    ciudades: List<String>, ciudad: String, onCiudadChange: (String) -> Unit
) {
    DropdownSelector(label = "Ciudad de la Experiencia", opciones = ciudades, seleccion = ciudad, onSeleccion = onCiudadChange, icon = Icons.Default.Map)
}

// Composable Auxiliar Reutilizable para los Dropdowns
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String,
    opciones: List<String>,
    seleccion: String,
    onSeleccion: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = seleccion,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            leadingIcon = { Icon(icon, contentDescription = null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.LightGray),
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false },
            modifier = Modifier.background(Color.White)
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSeleccion(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}
// --- IA PLANNER CON SLIDER INTERACTIVO ---

@Composable
fun IAPlannerSection(navController: NavController) {
    var presupuesto by remember { mutableFloatStateOf(2800f) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BluePrimary),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Alta Curaduría Digital", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(
                "Itinerarios Asistidos por IA",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Presupuesto Estimado:", color = Color.White, fontSize = 14.sp)
                Text(
                    text = "$${presupuesto.roundToInt()} USD",
                    color = GoldAccent,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Slider(
                value = presupuesto,
                onValueChange = { presupuesto = it },
                valueRange = 500f..10000f,
                steps = 18,
                colors = SliderDefaults.colors(
                    thumbColor = GoldAccent,
                    activeTrackColor = GoldAccent,
                    inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                ),
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    navController.navigate("itinerario?presupuesto=${presupuesto.roundToInt()}")
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Generar Borrador con IA", color = BluePrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun NavegacionInferiorCliente(navController: NavController, rutaActual: String) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = BluePrimary
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = rutaActual == "home_cliente",
            onClick = { if (rutaActual != "home_cliente") navController.navigate("home_cliente") { popUpTo(0) } }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ConfirmationNumber, contentDescription = "Reservas") },
            label = { Text("Reservas") },
            selected = rutaActual == "reservas",
            onClick = { if (rutaActual != "reservas") navController.navigate("reservas") { popUpTo(0) } }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "IA Planner") },
            label = { Text("IA Planner") },
            selected = rutaActual == "itinerario",
            onClick = {
                if (rutaActual != "itinerario") {
                    navController.navigate("itinerario?presupuesto=2800")
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Luggage, contentDescription = "Mis Viajes") },
            label = { Text("Mis Viajes") },
            selected = rutaActual == "mis_viajes",
            onClick = { if (rutaActual != "mis_viajes") navController.navigate("mis_viajes") { popUpTo(0) } }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") },
            selected = rutaActual == "perfil",
            onClick = { if (rutaActual != "perfil") navController.navigate("perfil") { popUpTo(0) } }
        )
    }
}