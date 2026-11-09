package com.example.globaltours

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth // 1. Agrega esta importación

object SupabaseProvider {
    val client = createSupabaseClient(
        supabaseUrl = "https://rxxitersgdsqnszrxblz.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJ4eGl0ZXJzZ2RzcW5zenJ4Ymx6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTMxNDc4MDksImV4cCI6MjEwODcyMzgwOX0.2Qm3hqeCyLh0lgUxOiVF6SmMCnvzTms1nVTSamQC0s8"
    ) {
        install(plugin = Postgrest)
        install(Auth) // 2. Instala el módulo de autenticación
    }
}