package com.example.quotevault.data



import android.util.Log
import com.example.quotevault.data.model.Quote
import com.example.quotevault.data.model.QuoteCategory
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.tasks.await
import javax.inject.Inject



    suspend fun seedQuotes() {
        Log.d("SEED", "seedQuotes() COMPLETED")

        val firestore = FirebaseFirestore.getInstance()
        val batch = firestore.batch()

        val quotes = listOf(
            mapOf(
                "content" to "The journey of a thousand miles begins with a single step.",
                "author" to "Lao Tzu",
                "category" to "MOTIVATION",
                "tags" to listOf("journey", "start", "discipline"),
                "likes" to 120,
                "isFeatured" to true,
                "createdAt" to Timestamp.now()
            ),
            mapOf(
                "content" to "Love is composed of a single soul inhabiting two bodies.",
                "author" to "Aristotle",
                "category" to "LOVE",
                "tags" to listOf("love", "connection"),
                "likes" to 180,
                "isFeatured" to true,
                "createdAt" to Timestamp.now()
            ),
            mapOf(
                "content" to "Success is not final, failure is not fatal: it is the courage to continue that counts.",
                "author" to "Winston Churchill",
                "category" to "SUCCESS",
                "tags" to listOf("success", "courage"),
                "likes" to 210,
                "isFeatured" to true,
                "createdAt" to Timestamp.now()
            ),
            mapOf(
                "content" to "Life is what happens when you’re busy making other plans.",
                "author" to "John Lennon",
                "category" to "LIFE",
                "tags" to listOf("life", "present"),
                "likes" to 200,
                "isFeatured" to true,
                "createdAt" to Timestamp.now()
            ),
            mapOf(
                "content" to "Happiness depends upon ourselves.",
                "author" to "Aristotle",
                "category" to "HAPPINESS",
                "tags" to listOf("happiness", "mindset"),
                "likes" to 145,
                "isFeatured" to true,
                "createdAt" to Timestamp.now()
            )
        )

        quotes.forEach { quote ->
            val docRef = firestore.collection("quotes").document()
            batch.set(docRef, quote)
        }

        batch.commit().await()
        Log.d("SEED", "seedQuotes() COMPLETED")

    }
