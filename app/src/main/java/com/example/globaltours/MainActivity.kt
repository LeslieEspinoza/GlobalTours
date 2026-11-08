package com.example.globaltours

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GlobalToursApp()
                }
            }
        }
    }
}

@Composable
fun GlobalToursApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("registro") { RegistroScreen(navController) }

        composable("admin_dashboard") { Text("Panel de Administrador / Jefe") }
        composable("staff_dashboard") { Text("Panel de Staff / Empleado") }

        // Rutas del cliente
        composable("home_cliente") { HomeClienteScreen(navController) }
        composable("itinerario") { ItinerarioScreen(navController) }
        composable("personalizar") { PersonalizarScreen(navController) }
        composable("confirmacion") { ConfirmacionScreen(navController) }
        composable("voucher") { VoucherScreen(navController) }
        composable("mis_viajes") { MisViajesScreen(navController) }
        composable("perfil") { PerfilScreen(navController) }
        composable("reservas") { ReservasScreen(navController) }
        composable("notificaciones") { NotificacionesScreen(navController) }
        composable("chat_soporte") { ChatSoporteScreen(navController) }
        composable("mis_datos") { MisDatosScreen(navController) }
        composable("preferencias") { PreferenciasScreen(navController) }
        composable("metodos_pago") { MetodosPagoScreen(navController) }
    }
}

@Composable
fun SplashScreen(navController: NavController) {
    // Efecto para esperar 2.5 segundos y navegar al Login
    LaunchedEffect(key1 = true) {
        delay(2500L)
        navController.navigate("login") {
            // Limpia el Splash Screen del historial para que el usuario no pueda volver a él con el botón "Atrás"
            popUpTo("splash") { inclusive = true }
        }
    }

    // Diseño visual del Splash Screen
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Asegúrate de que el nombre coincida con el archivo que pegaste en res/drawable
        Image(
            painter = painterResource(id = R.drawable.logo_globaltours),
            contentDescription = "Logotipo GlobalTours",
            modifier = Modifier.size(250.dp)
        )
    }
}

