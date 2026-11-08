package com.example.globaltours

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth // 1. Agrega esta importación

object SupabaseProvider {
    val client = createSupabaseClient(
        supabaseUrl = "https://rxxitersgdsqnszrxblz.supabase.co",
        supabaseKey = "sb_publishable_z0tTz5tFbJH60kNEhcoJnA_t5xYKf2l"
    ) {
        install(plugin = Postgrest)
        install(Auth) // 2. Instala el módulo de autenticación
    }
}