package com.bgsm.app.data.repository

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.bgsm.app.data.firebase.FirebaseErrorHandler
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

interface AuthRepository {
    val currentUser: FirebaseUser?
    val authStateFlow: Flow<FirebaseUser?>
    suspend fun registerWithEmail(displayName: String, email: String, password: String): Result<FirebaseUser>
    suspend fun loginWithEmail(email: String, password: String): Result<FirebaseUser>
    suspend fun signInWithCredential(credential: AuthCredential): Result<FirebaseUser>
    suspend fun sendPasswordReset(email: String): Result<Unit>
    suspend fun signOut(context: Context): Result<Unit>
}

class AuthRepositoryImpl(
    private val auth: FirebaseAuth = Firebase.auth,
    private val userRepository: UserRepository = UserRepositoryImpl()
) : AuthRepository {

    private val TAG = "[com.bgsm.app] AuthRepository"

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override val authStateFlow: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }

    override suspend fun registerWithEmail(
        displayName: String,
        email: String,
        password: String
    ): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val user = result.user ?: throw IllegalStateException("Firebase user was null after registration.")

            // Update Firebase Auth profile display name
            try {
                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName.trim())
                    .build()
                user.updateProfile(profileUpdate).await()
            } catch (e: Exception) {
                Log.w(TAG, "Failed to update Firebase Auth displayName", e)
            }

            // Create initial Firestore user document
            val profileResult = userRepository.createOrUpdateProfileFromAuth(user)
            if (profileResult.isFailure) {
                Log.w(TAG, "Initial Firestore profile creation failed, but user created in Auth: ${profileResult.exceptionOrNull()?.message}")
            }

            Log.i(TAG, "Successfully registered user with UID: ${user.uid}")
            Result.success(user)
        } catch (e: Exception) {
            val friendly = FirebaseErrorHandler.getFriendlyErrorMessage(e)
            Result.failure(Exception(friendly, e))
        }
    }

    override suspend fun loginWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val user = result.user ?: throw IllegalStateException("Firebase user was null after login.")

            // Ensure profile exists in Firestore
            userRepository.createOrUpdateProfileFromAuth(user)

            Log.i(TAG, "Successfully logged in user with UID: ${user.uid}")
            Result.success(user)
        } catch (e: Exception) {
            val friendly = FirebaseErrorHandler.getFriendlyErrorMessage(e)
            Result.failure(Exception(friendly, e))
        }
    }

    override suspend fun signInWithCredential(credential: AuthCredential): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithCredential(credential).await()
            val user = result.user ?: throw IllegalStateException("Firebase user was null after credential sign-in.")

            // Ensure Firestore profile is populated/preserved
            userRepository.createOrUpdateProfileFromAuth(user)

            Log.i(TAG, "Successfully authenticated with credential. UID: ${user.uid}")
            Result.success(user)
        } catch (e: Exception) {
            val friendly = FirebaseErrorHandler.getFriendlyErrorMessage(e)
            Result.failure(Exception(friendly, e))
        }
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email.trim()).await()
            Log.i(TAG, "Password reset email dispatched to: $email")
            Result.success(Unit)
        } catch (e: Exception) {
            val friendly = FirebaseErrorHandler.getFriendlyErrorMessage(e)
            Result.failure(Exception(friendly, e))
        }
    }

    override suspend fun signOut(context: Context): Result<Unit> {
        return try {
            auth.signOut()
            try {
                val credentialManager = CredentialManager.create(context)
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                Log.w(TAG, "Credential state clear failed", e)
            }
            Log.i(TAG, "User signed out successfully.")
            Result.success(Unit)
        } catch (e: Exception) {
            val friendly = FirebaseErrorHandler.getFriendlyErrorMessage(e)
            Result.failure(Exception(friendly, e))
        }
    }
}
