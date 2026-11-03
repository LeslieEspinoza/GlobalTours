package com.example.globaltours



import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// 1. Tabla Clientes
@Serializable
data class Cliente(
    val id: Long? = null,
    val nombres: String,
    val apellidos: String,
    val edad: Int,
    val telefono: String,
    val direccion: String,
    val pais: String,
    val email: String,
    val password: String,
    @SerialName("fecha_registro")
    val fechaRegistro: String? = null
)

// 2. Tabla Usuarios (Personal interno)
@Serializable
data class Usuario(
    val id: Long? = null,
    val nombres: String,
    val apellidos: String,
    val email: String,
    val password: String,
    val rol: String, // "empleado", "jefe", "admin"
    @SerialName("fecha_creacion")
    val fechaCreacion: String? = null
)

// 3. Tabla Citas (Cotizaciones)
@Serializable
data class Cita(
    val id: Long? = null,
    val nombre: String? = null,
    val email: String? = null,
    val telefono: String? = null,
    val destino: String? = null,
    @SerialName("numero_viajeros")
    val numeroViajeros: Int? = null, // <-- Ahora acepta valores nulos
    val presupuesto: String? = null, // <-- Ahora acepta valores nulos
    @SerialName("fecha_solicitud")
    val fechaSolicitud: String? = null
)