package com.example.quotevault.quotes



import android.R.attr.category
import com.example.quotevault.data.model.Quote
import com.example.quotevault.data.model.QuoteCategory
import com.example.quotevault.data.remote.SupabaseProvider

import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import kotlinx.coroutines.flow.Flow

@Singleton
class QuoteRepositoryImpl @Inject constructor() : QuoteRepository {

    private val supabase = SupabaseProvider.client
    private val auth = supabase.auth

    /* ---------- AUTH ---------- */

    override suspend fun signIn(email: String, password: String): Result<Unit> =
        runCatching {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
        }

    override suspend fun signUp(
        email: String,
        password: String,
        name: String
    ): Result<Unit> =
        runCatching {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = buildJsonObject {
                    put("name", name)
                }
            }
        }

    override suspend fun signOut(): Result<Unit> =
        runCatching { auth.signOut() }

    override suspend fun resetPassword(email: String): Result<Unit> =
        runCatching { auth.resetPasswordForEmail(email) }

    override suspend fun getCurrentUser(): Any? =
        auth.currentUserOrNull()

    /* ---------- QUOTES ---------- */

    override suspend fun getQuotes(): Result<List<Quote>> =
        runCatching {
            supabase
                .from("quotes")
                .select()
                .decodeList()
        }

    override suspend fun getQuotesByCategory(
        category: QuoteCategory
    ): Result<List<Quote>> =
        runCatching {
            supabase
                .from("quotes")
                .select {
                    filter {
                        eq("category", category.name)
                    }
                }
                .decodeList()
        }

    override suspend fun searchQuotes(query: String): Result<List<Quote>> =
        runCatching {
            supabase
                .from("quotes")
                .select {
                    filter {
                        ilike("content", "%$query%")
                    }
                }
                .decodeList()
        }

    override suspend fun getQuoteOfTheDay(): Result<Quote?> =
        runCatching {
            supabase
                .from("quotes")
                .select {
                    filter {
                        eq("is_featured", true)
                    }
                    limit(1)
                }
                .decodeList<Quote>()
                .firstOrNull()
        }

    /* ---------- FAVORITES ---------- */

    override fun getUserFavorites(): Flow<List<String>> = flow {
        val userId = auth.currentUserOrNull()?.id ?: return@flow
        val result = supabase
            .from("user_favorites")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<Map<String, String>>()

        emit(result.mapNotNull { it["quote_id"] })
    }
    override suspend fun getQuoteById(
        quoteId: String
    ): Result<Quote?> =
        runCatching {
            supabase
                .from("quotes")
                .select {
                    filter {
                        eq("id", quoteId)
                    }
                    limit(1)
                }
                .decodeList<Quote>()
                .firstOrNull()
        }
    override suspend fun getFavoriteQuotes(): Result<List<Quote>> =
        runCatching {
            val userId = auth.currentUserOrNull()?.id
                ?: throw Exception("User not logged in")

            // 1️⃣ Get favorite quote IDs
            val favoriteRows = supabase
                .from("user_favorites")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<Map<String, String>>()

            val quoteIds = favoriteRows.mapNotNull { it["quote_id"] }

            if (quoteIds.isEmpty()) return@runCatching emptyList()

            // 2️⃣ Fetch ALL quotes, then filter locally (SAFE & SIMPLE)
            val allQuotes = supabase
                .from("quotes")
                .select()
                .decodeList<Quote>()

            allQuotes.filter { it.id in quoteIds }
        }

    override suspend fun addToFavorites(quoteId: String): Result<Unit> =
        runCatching {
            val userId = auth.currentUserOrNull()?.id
                ?: throw Exception("User not logged in")

            supabase.from("user_favorites").insert(
                mapOf(
                    "user_id" to userId,
                    "quote_id" to quoteId
                )
            )
        }

    override suspend fun removeFromFavorites(quoteId: String): Result<Unit> =
        runCatching {
            val userId = auth.currentUserOrNull()?.id
                ?: throw Exception("User not logged in")

            supabase
                .from("user_favorites")
                .delete {
                    filter {
                        eq("user_id", userId)
                        eq("quote_id", quoteId)
                    }
                }
        }
}
