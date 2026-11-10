package com.example.globaltours

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.core.content.edit

@Composable
fun OnboardingScreen(navController: NavController) {
    val context = LocalContext.current
    var paginaActual by remember { mutableIntStateOf(0) }

    val titulos = listOf(
        "Descubre el Mundo",
        "Asistencia Inteligente",
        "Gestiona tus Viajes"
    )
    val descripciones = listOf(
        "Encuentra y reserva los mejores paquetes turísticos y vuelos desde la comodidad de tu celular.",
        "Nuestro asistente de IA y asesores humanos están listos para resolver tus dudas 24/7.",
        "Accede a tus vouchers, revisa el estado de tus pagos y mantén todo tu itinerario organizado."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBeige)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Contenedor del Logo
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(BluePrimary.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_globaltours), // <- Paréntesis corregido aquí
                contentDescription = "Logo de GlobalTours",
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = titulos[paginaActual],
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = BluePrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = descripciones[paginaActual],
            fontSize = 15.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Indicadores de página (Puntitos)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (i in 0..2) {
                Box(
                    modifier = Modifier
                        .size(if (i == paginaActual) 12.dp else 8.dp)
                        .clip(CircleShape)
                        .background(if (i == paginaActual) BluePrimary else Color.LightGray)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón de Siguiente / Comenzar
        Button(
            onClick = {
                if (paginaActual < 2) {
                    paginaActual++
                } else {
                    // Guardar que ya vio el tutorial
                    val sharedPreferences = context.getSharedPreferences("GlobalToursPrefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit { putBoolean("ONBOARDING_COMPLETADO", true) }

                    // Navegar al login
                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
        ) {
            Text(
                text = if (paginaActual == 2) "Comenzar Aventura" else "Siguiente",
                color = BluePrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            if (paginaActual < 2) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = BluePrimary)
            }
        }
    }
}