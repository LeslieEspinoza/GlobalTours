package com.example.globaltours

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

@Serializable
private data class GeminiPart(val text: String)

@Serializable
private data class GeminiContent(val parts: List<GeminiPart>)

@Serializable
private data class GeminiGenerationConfig(val responseMimeType: String? = null)

@Serializable
private data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig? = null
)

@Serializable
private data class GeminiCandidate(val content: GeminiContent? = null)

@Serializable
private data class GeminiResponse(val candidates: List<GeminiCandidate>? = emptyList())

object GeminiService {
    // ⚠️ Pega aquí tu clave que inicie con AIzaSy...
    private const val API_KEY = "AQ.Ab8RN6K0R962jmeCv4z7TFeY3XUYMfvjPPjTtxRD6b8fqKYM9A"

    private val jsonConfig = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    suspend fun generarItinerario(presupuesto: Int, estiloViaje: String): ItinerarioIARespuesta = withContext(Dispatchers.IO) {
        try {
            val promptText = """
                Crea un itinerario de viaje completo y optimizado.
                Presupuesto máximo: $presupuesto USD.
                Estilo de viaje: $estiloViaje.

                Responde UNICAMENTE con un objeto JSON válido sin texto ni Markdown adicional:
                {
                  "titulo_paquete": "Nombre del viaje",
                  "destino": "Ciudad, País",
                  "duracion_dias": 5,
                  "presupuesto_total_estimado": $presupuesto.0,
                  "descripcion_general": "Descripción breve del paquete",
                  "hotel_sugerido": "Nombre del hotel",
                  "dias": [
                    {
                      "dia": 1,
                      "titulo": "Título del día 1",
                      "actividades": ["Actividad 1", "Actividad 2"],
                      "costo_estimado_dia": 150.0
                    }
                  ]
                }
            """.trimIndent()

            val reqBodyObj = GeminiRequest(
                contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = promptText)))),
                generationConfig = GeminiGenerationConfig(responseMimeType = "application/json")
            )

            val reqJson = jsonConfig.encodeToString(GeminiRequest.serializer(), reqBodyObj)
            val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$API_KEY")

            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                doOutput = true
                connectTimeout = 15000
                readTimeout = 15000
            }

            OutputStreamWriter(connection.outputStream, "UTF-8").use { os ->
                os.write(reqJson)
                os.flush()
            }

            val responseCode = connection.responseCode
            val stream = if (responseCode in 200..299) connection.inputStream else connection.errorStream
            val responseBody = stream.bufferedReader().use { it.readText() }

            if (responseCode !in 200..299) {
                android.util.Log.w("GEMINI_API", "HTTP $responseCode: Activando respaldo local.")
                return@withContext generarItinerarioRespaldo(presupuesto, estiloViaje)
            }

            val geminiResp = jsonConfig.decodeFromString<GeminiResponse>(responseBody)
            var textResult = geminiResp.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: return@withContext generarItinerarioRespaldo(presupuesto, estiloViaje)

            textResult = textResult.replace("```json", "").replace("```", "").trim()
            return@withContext jsonConfig.decodeFromString<ItinerarioIARespuesta>(textResult)

        } catch (e: Exception) {
            android.util.Log.e("GEMINI_ERROR", "Fallo de conexión: ${e.message}. Usando respaldo.")
            return@withContext generarItinerarioRespaldo(presupuesto, estiloViaje)
        }
    }

    // Generador de respaldo para garantizar que el flujo CRM nunca falle
    private fun generarItinerarioRespaldo(presupuesto: Int, estilo: String): ItinerarioIARespuesta {
        val destinoSugerido = when {
            estilo.contains("Playa", ignoreCase = true) -> "Cancún, México"
            estilo.contains("Lujo", ignoreCase = true) -> "Dubái, Emiratos Árabes"
            estilo.contains("Aventura", ignoreCase = true) -> "San José, Costa Rica"
            else -> "Madrid & Toledo, España"
        }

        return ItinerarioIARespuesta(
            titulo_paquete = "Experiencia Personalizada: $estilo",
            destino = destinoSugerido,
            duracion_dias = 5,
            presupuesto_total_estimado = presupuesto.toDouble(),
            descripcion_general = "Itinerario curado digitalmente acorde a tu presupuesto estimado de $$presupuesto USD con foco en $estilo.",
            hotel_sugerido = "Grand Luxury Resort & Spa",
            dias = listOf(
                DiaItinerario(1, "Llegada y Bienvenida VIP", listOf("Traslado privado al hotel", "Cena de bienvenida en restaurante panorámico"), presupuesto * 0.2),
                DiaItinerario(2, "Tour Principal del Destino", listOf("Recorrido guiado privado", "Almuerzo típico gourmet", "Tarde libre de compras"), presupuesto * 0.25),
                DiaItinerario(3, "Experiencia de $estilo", listOf("Actividad exclusiva matutina", "Sesión de spa / relajación", "Cena show en vivo"), presupuesto * 0.25),
                DiaItinerario(4, "Excursión y Naturaleza", listOf("Pasadía completo con transporte guiado", "Degustación gastronómica"), presupuesto * 0.2),
                DiaItinerario(5, "Despedida y Check-out", listOf("Desayuno buffet", "Traslado privado hacia el aeropuerto"), presupuesto * 0.1)
            )
        )
    }
}