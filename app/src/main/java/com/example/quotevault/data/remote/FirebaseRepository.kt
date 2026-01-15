package com.example.quotevault.data.remote



import com.example.quotevault.data.model.Collection
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.example.quotevault.data.model.Quote
import com.example.quotevault.data.model.QuoteCategory
import com.example.quotevault.data.model.User
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    fun getAuthStateFlow(): Flow<com.google.firebase.auth.FirebaseUser?> {
        return callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                trySend(auth.currentUser)
            }
            auth.addAuthStateListener(listener)

            awaitClose {
                auth.removeAuthStateListener(listener)
            }
        }
    }

    suspend fun updateUserSettings(userId: String, updates: Map<String, Any>) {
        firestore.collection("users").document(userId)
            .update(updates)
            .await()
    }

    suspend fun getQuoteById(quoteId: String): Quote {
        val document = firestore.collection("quotes")
            .document(quoteId)
            .get()
            .await()

        return document.toObject<Quote>()?.copy(id = document.id)
            ?: throw Exception("Quote not found")
    }
    // Authentication
    suspend fun signUp(email: String, password: String, displayName: String): User {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw Exception("User creation failed")

        // Create user profile in Firestore
        val newUser = User(
            uid = user.uid,
            email = user.email ?: "",
            displayName = displayName
        )

        firestore.collection("users").document(user.uid).set(newUser).await()
        return newUser
    }

    suspend fun signIn(email: String, password: String): User {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw Exception("Sign in failed")

        return getUser(user.uid)
    }

    suspend fun signOut() {
        auth.signOut()
    }

    suspend fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    suspend fun updateProfile(displayName: String?, photoUrl: String?) {
        val user = auth.currentUser ?: throw Exception("User not logged in")

        val updates = HashMap<String, Any>()
        displayName?.let { updates["displayName"] = it }
        photoUrl?.let { updates["photoUrl"] = it }

        firestore.collection("users").document(user.uid).update(updates).await()
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    suspend fun getUser(uid: String): User {
        val document = firestore.collection("users").document(uid).get().await()
        return document.toObject<User>() ?: throw Exception("User not found")
    }

    // Quotes
    suspend fun getQuotes(limit: Int = 20, lastDocumentId: String? = null): List<Quote> {
        var query = firestore.collection("quotes")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit.toLong())

        lastDocumentId?.let {
            val lastDoc = firestore.collection("quotes").document(it).get().await()
            query = query.startAfter(lastDoc)
        }

        val snapshot = query.get().await()
        return snapshot.documents.mapNotNull { it.toObject<Quote>()?.copy(id = it.id) }
    }

    suspend fun getQuotesByCategory(category: QuoteCategory, limit: Int = 20): List<Quote> {
        val snapshot = firestore.collection("quotes")
            .whereEqualTo("category", category.name)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.toObject<Quote>()?.copy(id = it.id) }
    }

    suspend fun searchQuotes(query: String): List<Quote> {
        // Note: Firestore doesn't support OR queries directly
        val contentSnapshot = firestore.collection("quotes")
            .whereGreaterThanOrEqualTo("content", query)
            .whereLessThanOrEqualTo("content", query + "\uf8ff")
            .get()
            .await()

        val authorSnapshot = firestore.collection("quotes")
            .whereGreaterThanOrEqualTo("author", query)
            .whereLessThanOrEqualTo("author", query + "\uf8ff")
            .get()
            .await()

        val combined = contentSnapshot.documents + authorSnapshot.documents
        return combined.distinctBy { it.id }
            .mapNotNull { it.toObject<Quote>()?.copy(id = it.id) }
    }

    suspend fun getQuoteOfTheDay(): Quote {
        val snapshot = firestore.collection("quotes")
            .whereEqualTo("isFeatured", true)
            .limit(1)
            .get()
            .await()

        return snapshot.documents.firstOrNull()?.toObject<Quote>()?.copy(
            id = snapshot.documents.first().id
        ) ?: throw Exception("No featured quote found")
    }

    // Favorites
    suspend fun addToFavorites(userId: String, quoteId: String) {
        firestore.collection("users").document(userId)
            .collection("favorites").document(quoteId)
            .set(mapOf("quoteId" to quoteId, "addedAt" to System.currentTimeMillis()))
            .await()
    }

    suspend fun removeFromFavorites(userId: String, quoteId: String) {
        firestore.collection("users").document(userId)
            .collection("favorites").document(quoteId)
            .delete()
            .await()
    }

    fun getUserFavorites(userId: String): Flow<List<String>> = callbackFlow {
        val listener = firestore.collection("users").document(userId)
            .collection("favorites")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val favorites = snapshot?.documents?.map { it.id } ?: emptyList()
                trySend(favorites)
            }

        awaitClose { listener.remove() }
    }

    // Collections
    suspend fun createCollection(userId: String, name: String, description: String = ""): String {
        val collectionRef = firestore.collection("users").document(userId)
            .collection("collections").document()

        val collection = Collection(
            id = collectionRef.id,
            name = name,
            description = description,
            userId = userId
        )

        collectionRef.set(collection).await()
        return collectionRef.id
    }

    suspend fun addToCollection(collectionId: String, userId: String, quoteId: String) {
        firestore.collection("users").document(userId)
            .collection("collections").document(collectionId)
            .collection("quotes").document(quoteId)
            .set(mapOf("quoteId" to quoteId, "addedAt" to System.currentTimeMillis()))
            .await()
    }


    // Seed Quotes (Run once)
    suspend fun seedQuotes(quotes: List<Quote>) {
        val batch = firestore.batch()
        quotes.forEach { quote ->
            val docRef = firestore.collection("quotes").document()
            batch.set(docRef, quote.copy(id = docRef.id))
        }
        batch.commit().await()
    }
    // Add to your existing FirebaseRepository class

    // Favorites
    suspend fun getFavoriteQuotes(userId: String): List<Quote> {
        val favoritesSnapshot = firestore.collection("users").document(userId)
            .collection("favorites").get().await()

        if (favoritesSnapshot.isEmpty) return emptyList()

        val quoteIds = favoritesSnapshot.documents.map { it.id }

        // Firestore doesn't support IN queries with more than 10 items
        // So we need to chunk the IDs
        val quotes = mutableListOf<Quote>()

        quoteIds.chunked(10).forEach { chunk ->
            val quotesSnapshot = firestore.collection("quotes")
                .whereIn(FieldPath.documentId(), chunk)
                .get()
                .await()

            quotes.addAll(quotesSnapshot.documents.mapNotNull {
                it.toObject<Quote>()?.copy(id = it.id)
            })
        }

        return quotes
    }

    // Collections
    suspend fun removeFromCollection(collectionId: String, userId: String, quoteId: String) {
        firestore.collection("users").document(userId)
            .collection("collections").document(collectionId)
            .collection("quotes").document(quoteId)
            .delete()
            .await()
    }

    suspend fun getUserCollections(userId: String): List<Collection> {
        val snapshot = firestore.collection("users").document(userId)
            .collection("collections")
            .get()
            .await()

        return snapshot.documents.mapNotNull {
            it.toObject<Collection>()?.copy(id = it.id)
        }
    }

// Also add this import at the top of FirebaseRepository.kt:
//    import com.google.firebase.firestore.FieldPath
}