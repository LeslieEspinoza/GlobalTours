package com.example.globaltours

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultadosBusquedaScreen(
    navController: NavController,
    tipo: String,
    origen: String = "",
    destino: String = ""
) {
    var cargando by remember { mutableStateOf(true) }
    var listaVuelos by remember { mutableStateOf<List<Vuelo>>(emptyList()) }
    var listaHoteles by remember { mutableStateOf<List<Hotel>>(emptyList()) }
    var listaExcursiones by remember { mutableStateOf<List<Excursion>>(emptyList()) }
    val scope = rememberCoroutineScope()

    fun crearReserva(tipoServicio: String, titulo: String, detalle: String, precio: Double) {
        scope.launch {
            try {
                val userId = SupabaseProvider.client.auth.currentUserOrNull()?.id
                if (userId != null) {
                    val nuevaReserva = ReservaModel(
                        auth_id = userId,
                        tipo_servicio = tipoServicio,
                        titulo = titulo,
                        detalle = detalle,
                        precio = precio,
                        estado = "Pendiente de Pago"
                    )
                    SupabaseProvider.client.from("reservas").insert(nuevaReserva)
                    navController.navigate("reservas") {
                        popUpTo("home_cliente")
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("RESERVA_ERROR", "Error al crear reserva: ${e.message}", e)
            }
        }
    }

    LaunchedEffect(tipo, origen, destino) {
        try {
            when (tipo) {
                "Vuelos" -> {
                    listaVuelos = SupabaseProvider.client.from("vuelos").select {
                        filter {
                            eq("origen", origen)
                            eq("destino", destino)
                        }
                    }.decodeList<Vuelo>()
                }
                "Hoteles" -> {
                    listaHoteles = SupabaseProvider.client.from("hoteles").select {
                        filter {
                            eq("ciudad", destino)
                        }
                    }.decodeList<Hotel>()
                }
                "Excursiones" -> {
                    listaExcursiones = SupabaseProvider.client.from("excursiones").select {
                        filter {
                            eq("ciudad", destino)
                        }
                    }.decodeList<Excursion>()
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("RESULTADOS_ERROR", "Error consultando $tipo: ${e.message}", e)
        } finally {
            cargando = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Disponibilidad de $tipo", fontWeight = FontWeight.Bold, color = BluePrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = BluePrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = BackgroundBeige
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (cargando) {
                CircularProgressIndicator(color = BluePrimary, modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (tipo) {
                        "Vuelos" -> {
                            if (listaVuelos.isEmpty()) item { SinResultados() }
                            items(listaVuelos) { vuelo ->
                                TarjetaVueloItem(
                                    vuelo = vuelo,
                                    onSeleccionar = {
                                        crearReserva(
                                            tipoServicio = "Vuelo",
                                            titulo = vuelo.aerolinea,
                                            detalle = "${vuelo.origen} ➔ ${vuelo.destino}",
                                            precio = vuelo.precio
                                        )
                                    }
                                )
                            }
                        }
                        "Hoteles" -> {
                            if (listaHoteles.isEmpty()) item { SinResultados() }
                            items(listaHoteles) { hotel ->
                                TarjetaHotelItem(
                                    hotel = hotel,
                                    onReservar = {
                                        crearReserva(
                                            tipoServicio = "Hotel",
                                            titulo = hotel.nombre,
                                            detalle = "Estadía en ${hotel.ciudad}",
                                            precio = hotel.precio_noche
                                        )
                                    }
                                )
                            }
                        }
                        "Excursiones" -> {
                            if (listaExcursiones.isEmpty()) item { SinResultados() }
                            items(listaExcursiones) { excursion ->
                                TarjetaExcursionItem(
                                    excursion = excursion,
                                    onReservar = {
                                        crearReserva(
                                            tipoServicio = "Excursión",
                                            titulo = excursion.titulo,
                                            detalle = "Experiencia de ${excursion.duracion_horas}h en ${excursion.ciudad}",
                                            precio = excursion.precio
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SinResultados() {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
            Text("No se encontraron opciones disponibles para tu selección.", color = Color.Gray)
        }
    }
}

@Composable
fun TarjetaVueloItem(vuelo: Vuelo, onSeleccionar: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(vuelo.aerolinea, fontWeight = FontWeight.Bold, color = BluePrimary, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("${vuelo.origen} ➔ ${vuelo.destino}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text("Horario: ${vuelo.horario}", color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("$${vuelo.precio.toInt()} USD", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = BluePrimary)
                Button(onClick = onSeleccionar, colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)) {
                    Text("Seleccionar", color = BluePrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TarjetaHotelItem(hotel: Hotel, onReservar: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(16.dp)) {
            hotel.imagen_url?.let { url ->
                AsyncImage(model = url, contentDescription = null, modifier = Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(hotel.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                Text(" ${hotel.estrellas} Estrellas", fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("$${hotel.precio_noche.toInt()} USD / Noche", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = BluePrimary)
                Button(onClick = onReservar, colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)) {
                    Text("Reservar Hotel", color = BluePrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TarjetaExcursionItem(excursion: Excursion, onReservar: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(16.dp)) {
            excursion.imagen_url?.let { url ->
                AsyncImage(model = url, contentDescription = null, modifier = Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(excursion.titulo, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
            Text("Duración: ${excursion.duracion_horas} Horas", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("$${excursion.precio.toInt()} USD", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = BluePrimary)
                Button(onClick = onReservar, colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)) {
                    Text("Ver Detalles", color = BluePrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}