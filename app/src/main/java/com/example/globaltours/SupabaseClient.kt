package com.example.globaltours

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseProvider {
    val client = createSupabaseClient(
        supabaseUrl = "https://rxxitersgdsqnszrxblz.supabase.co",
        supabaseKey = "sb_publishable_zOtTz5tFbJH6OkNEhcoJnA_t5xYKf2l"
    ) {
        install(Postgrest)
    }
}

