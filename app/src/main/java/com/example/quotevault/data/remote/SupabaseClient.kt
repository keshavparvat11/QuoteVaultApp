package com.example.quotevault.data.remote

import android.util.Log
import com.example.quotevault.BuildConfig
import io.github.jan.supabase.createSupabaseClient


import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object SupabaseProvider {

    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY
    ) {
        install(Auth)
        install(Postgrest)
    }
}

fun testSupabaseConnection() {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val result = SupabaseProvider.client
                .from("quotes")
                .select {
                    limit(1)
                }

            Log.d("SUPABASE", "Connection OK: ${result.data}")
        } catch (e: Exception) {
            Log.e("SUPABASE", "Connection FAILED", e)
        }
    }
}