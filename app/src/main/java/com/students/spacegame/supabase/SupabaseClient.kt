package com.students.spacegame.supabase

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseClient @Inject constructor() {
    val client = createSupabaseClient(
        supabaseUrl = "https://your-project-id.supabase.co",
        supabaseKey = "your-anon-key-here"
    ) {
        install(Postgrest)
    }
}
