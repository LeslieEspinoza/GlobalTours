package com.example.globaltours

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
// import androidx.compose.material3.MaterialTheme
// import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Leemos la preferencia apenas se abre la app
        val sharedPreferences = getSharedPreferences("GlobalToursPrefs", Context.MODE_PRIVATE)
        val onboardingCompletado = sharedPreferences.getBoolean("ONBOARDING_COMPLETADO", false)

        // 2. Decidimos a dónde debe ir la app DESPUÉS del Splash Screen
        val destinoPostSplash = if (onboardingCompletado) "login" else "onboarding"

        setContent {
            // Pasamos la decisión directamente a tu función principal
            GlobalToursApp(destinoPostSplash = destinoPostSplash)
        }
    }
}

@Composable
fun GlobalToursApp(destinoPostSplash: String) {
    val navController = rememberNavController()

    // El NavHost AHORA INICIA en "splash"
    NavHost(navController = navController, startDestination = "splash") {

        // Le pasamos el destino final al SplashScreen
        composable("splash") { SplashScreen(navController, destinoPostSplash) }

        composable("onboarding") { OnboardingScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("registro") { RegistroScreen(navController) }

        composable("admin_dashboard") { Text("Panel de Administrador / Jefe") }
        composable("staff_dashboard") { Text("Panel de Staff / Empleado") }

        // Rutas del cliente
        composable("home_cliente") { HomeClienteScreen(navController) }
        composable("personalizar") { PersonalizarScreen(navController) }
        composable("confirmacion") { ConfirmacionScreen(navController) }
        composable("mis_viajes") { MisViajesScreen(navController) }
        composable("perfil") { PerfilClienteScreen(navController = navController) }
        composable("reservas") { ReservasScreen(navController) }
        composable("notificaciones") { NotificacionesScreen(navController) }
        composable("chat_soporte_ia") { ChatSoporteIAScreen(navController) }
        composable("agendar_cita_soporte") { AgendarCitaScreen(navController) }
        composable("mis_datos") { MisDatosScreen(navController) }
        composable("preferencias") { PreferenciasScreen(navController) }
        composable("metodos_pago") { MetodosPagoScreen(navController) }
        composable("todos_los_paquetes") {
            TodosLosPaquetesScreen(navController)
        }

        composable(
            route = "pasarela_pago?reservaId={reservaId}",
            arguments = listOf(
                navArgument("reservaId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val reservaId = backStackEntry.arguments?.getLong("reservaId") ?: 0L
            PasarelaPagoScreen(
                navController = navController,
                reservaId = reservaId
            )
        }

        composable(
            route = "voucher?id={id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val reservaId = backStackEntry.arguments?.getLong("id") ?: 0L
            VoucherScreen(
                navController = navController,
                reservaId = reservaId
            )
        }

        composable(
            route = "itinerario?presupuesto={presupuesto}",
            arguments = listOf(
                navArgument("presupuesto") {
                    type = NavType.IntType
                    defaultValue = 2800
                }
            )
        ) { backStackEntry ->
            val presupuesto = backStackEntry.arguments?.getInt("presupuesto") ?: 2800
            ItinerarioIAScreen(
                navController = navController,
                presupuestoInicial = presupuesto
            )
        }

        composable(
            route = "detalle_paquete?id={id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val paqueteId = backStackEntry.arguments?.getLong("id") ?: 0L
            DetallePaqueteScreen(
                navController = navController,
                paqueteId = paqueteId
            )
        }

        composable(
            route = "resultados_busqueda?tipo={tipo}&origen={origen}&destino={destino}",
            arguments = listOf(
                navArgument("tipo") { defaultValue = "Vuelos" },
                navArgument("origen") { defaultValue = "" },
                navArgument("destino") { defaultValue = "" }
            )
        ) { backStackEntry ->
            val tipo = backStackEntry.arguments?.getString("tipo") ?: "Vuelos"
            val origen = backStackEntry.arguments?.getString("origen") ?: ""
            val destino = backStackEntry.arguments?.getString("destino") ?: ""

            ResultadosBusquedaScreen(
                navController = navController,
                tipo = tipo,
                origen = origen,
                destino = destino
            )
        }
    }
}

@Composable
fun SplashScreen(navController: NavController, destinoPostSplash: String) {
    LaunchedEffect(key1 = true) {
        delay(2500L)
        // Navega dinámicamente al Onboarding o al Login dependiendo de lo que evaluó MainActivity
        navController.navigate(destinoPostSplash) {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_globaltours),
            contentDescription = "Logotipo GlobalTours",
            modifier = Modifier.size(250.dp)
        )
    }
}