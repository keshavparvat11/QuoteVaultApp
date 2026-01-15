package com.example.quotevault.auth
//
//import com.example.quotevault.data.remote.SupabaseProvider
//import io.github.jan.supabase.gotrue.auth
//import io.github.jan.supabase.gotrue.providers.builtin.Email
//import io.github.jan.supabase.gotrue.user.UserInfo
//
//
//class SupabaseAuthRepository {
//
//    private val auth = SupabaseProvider.client.auth
//
//    suspend fun signUp(
//        email: String,
//        password: String
//    ): Result<UserInfo> {
//        return try {
//            auth.signUpWith(Email) {
//                this.email = email
//                this.password = password
//            }
//
//            val user = auth.currentUserOrNull()
//                ?: return Result.failure(Exception("User not available after signup"))
//
//            Result.success(user)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//    suspend fun signIn(
//        email: String,
//        password: String
//    ): Result<UserInfo> {
//        return try {
//            auth.signInWith(Email) {
//                this.email = email
//                this.password = password
//            }
//
//            val user = auth.currentUserOrNull()
//                ?: return Result.failure(Exception("User not available after login"))
//
//            Result.success(user)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//    suspend fun signOut(): Result<Unit> {
//        return try {
//            auth.signOut()
//            Result.success(Unit)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//    fun currentUser(): UserInfo? =
//        auth.currentUserOrNull()
//    fun isUserLoggedIn(): Boolean {
//        return SupabaseProvider.client.auth.currentSessionOrNull() != null
//    }
//}
