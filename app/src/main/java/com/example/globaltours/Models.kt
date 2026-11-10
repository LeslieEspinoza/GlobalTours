package com.example.globaltours

import kotlinx.serialization.Serializable


@Serializable
data class ClienteModel(
    val id: Long? = null,
    val auth_id: String? = null,
    val nombres: String? = null,    // Coincide con tu DB
    val apellidos: String? = null,  // Coincide con tu DB
    val email: String? = null,
    val telefono: String? = null,
    val pasaporte: String? = null
)

@Serializable
data class TicketSoporteModel(
    val id: Long? = null,
    val auth_id: String,
    val tipo: String,
    val asunto: String,
    val descripcion: String,
    val estado: String = "Pendiente"
)
@Serializable
data class PaqueteTuristico(
    val id: Long = 0,
    val titulo: String = "",
    val destino: String = "",
    val duracion_dias: Int = 0,
    val precio_base: Double = 0.0,
    val destacado: Boolean? = false,
    val imagen_url: String? = null,
    val descripcion: String? = null,
    val incluye: String? = null
)

@Serializable
data class Vuelo(
    val id: Long = 0,
    val origen: String = "",
    val destino: String = "",
    val aerolinea: String = "",
    val precio: Double = 0.0,
    val horario: String = ""
)

@Serializable
data class Hotel(
    val id: Long = 0,
    val nombre: String = "",
    val ciudad: String = "",
    val precio_noche: Double = 0.0,
    val estrellas: Int = 5,
    val imagen_url: String? = null
)

@Serializable
data class Excursion(
    val id: Long = 0,
    val titulo: String = "",
    val ciudad: String = "",
    val precio: Double = 0.0,
    val duracion_horas: Int = 0,
    val imagen_url: String? = null
)

@Serializable
data class ReservaModel(
    val id: Long = 0,
    val auth_id: String = "",
    val tipo_servicio: String = "",
    val titulo: String = "",
    val detalle: String = "",
    val precio: Double = 0.0,
    val estado: String = "Pendiente de Pago"
)

@Serializable
data class NotificacionModel(
    val id: Long = 0,
    val auth_id: String = "",
    val titulo: String = "",
    val mensaje: String = "",
    val tipo: String = "sistema",
    val leido: Boolean = false,
    val created_at: String? = null
)

@Serializable
data class DiaItinerario(
    val dia: Int = 1,
    val titulo: String = "",
    val actividades: List<String> = emptyList(),
    val costo_estimado_dia: Double = 0.0
)

@Serializable
data class ItinerarioIARespuesta(
    val titulo_paquete: String = "",
    val destino: String = "",
    val duracion_dias: Int = 0,
    val presupuesto_total_estimado: Double = 0.0,
    val descripcion_general: String = "",
    val hotel_sugerido: String = "",
    val dias: List<DiaItinerario> = emptyList()
)
@Serializable
data class CitaModel(
    val id: Long? = null,
    val nombre: String,
    val email: String,
    val telefono: String,
    val destino: String
)

@Serializable
data class EvaluacionClienteInsert(
    val calificacion: Int,
    val comentario: String,
    val paquete_nombre: String? = null
)