package com.example.globaltours

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacionesScreen(navController: NavController) {
    var listaNotificaciones by remember { mutableStateOf<List<NotificacionModel>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    fun cargarNotificaciones() {
        cargando = true
        scope.launch {
            try {
                val userId = SupabaseProvider.client.auth.currentUserOrNull()?.id
                if (userId != null) {
                    val notifs = SupabaseProvider.client.from("notifications")
                        .select {
                            filter {
                                eq("auth_id", userId)
                            }
                        }.decodeList<NotificacionModel>()

                    listaNotificaciones = notifs.sortedByDescending { it.id }
                }
            } catch (e: Exception) {
                android.util.Log.e("NOTIFICACIONES_ERROR", "Error al cargar: ${e.message}", e)
            } finally {
                cargando = false
            }
        }
    }

    LaunchedEffect(Unit) {
        cargarNotificaciones()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (cargando) {
                CircularProgressIndicator(color = BluePrimary, modifier = Modifier.align(Alignment.Center))
            } else if (listaNotificaciones.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No tienes notificaciones por el momento", color = Color.Gray, fontSize = 15.sp)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(listaNotificaciones) { notificacion ->
                        TarjetaNotificacionItem(
                            notificacion = notificacion,
                            onClick = {
                                if (!notificacion.leido) {
                                    scope.launch {
                                        try {
                                            SupabaseProvider.client.from("notifications").update({
                                                set("leido", true)
                                            }) {
                                                filter {
                                                    eq("id", notificacion.id)
                                                }
                                            }
                                            cargarNotificaciones()
                                        } catch (e: Exception) {
                                            android.util.Log.e("NOTIF_READ_ERROR", "Error: ${e.message}", e)
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaNotificacionItem(
    notificacion: NotificacionModel,
    onClick: () -> Unit
) {
    val (icono, colorIcono) = when (notificacion.tipo.lowercase()) {
        "reserva" -> Pair(Icons.Default.ConfirmationNumber, BluePrimary)
        "oferta" -> Pair(Icons.Default.LocalOffer, GoldAccent)
        else -> Pair(Icons.Default.Info, Color(0xFF1976D2))
    }

    val contenedorColor = if (!notificacion.leido) Color.White else Color(0xFFF5F5F5)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = contenedorColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (!notificacion.leido) 3.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(colorIcono.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notificacion.titulo,
                        fontSize = 15.sp,
                        fontWeight = if (!notificacion.leido) FontWeight.Bold else FontWeight.SemiBold,
                        color = Color.Black
                    )

                    if (!notificacion.leido) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(GoldAccent, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notificacion.mensaje,
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    lineHeight = 18.sp
                )
            }
        }
    }
}