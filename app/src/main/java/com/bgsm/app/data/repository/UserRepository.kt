package com.bgsm.app.data.repository

import android.util.Log
import com.bgsm.app.data.firebase.FirebaseErrorHandler
import com.bgsm.app.data.firebase.OperationType
import com.bgsm.app.data.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

interface UserRepository {
    suspend fun getUserProfile(uid: String): Result<UserProfile?>
    fun observeUserProfile(uid: String): Flow<UserProfile?>
    suspend fun createOrUpdateProfileFromAuth(user: FirebaseUser): Result<UserProfile>
    suspend fun updateUserProfile(uid: String, updates: Map<String, Any?>): Result<Unit>
}

class UserRepositoryImpl(
    private val db: FirebaseFirestore = Firebase.firestore
) : UserRepository {

    private val auth = Firebase.auth
    private val TAG = "[com.bgsm.app] UserRepository"

    private fun requireCurrentUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be authenticated before accessing profile data.")
    }

    override suspend fun getUserProfile(uid: String): Result<UserProfile?> {
        return try {
            val doc = db.collection("users").document(uid).get().await()
            val profile = if (doc.exists()) UserProfile.fromDocument(doc) else null
            Result.success(profile)
        } catch (e: Exception) {
            val msg = FirebaseErrorHandler.handleFirestoreError(e, OperationType.GET, "users/$uid")
            Result.failure(Exception(msg, e))
        }
    }

    override fun observeUserProfile(uid: String): Flow<UserProfile?> = callbackFlow {
        var listener: ListenerRegistration? = null
        try {
            listener = db.collection("users").document(uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        FirebaseErrorHandler.handleFirestoreError(error, OperationType.GET, "users/$uid")
                        close(error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        val profile = UserProfile.fromDocument(snapshot)
                        trySend(profile)
                    } else {
                        trySend(null)
                    }
                }
        } catch (e: Exception) {
            FirebaseErrorHandler.handleFirestoreError(e, OperationType.GET, "users/$uid")
            close(e)
        }

        awaitClose {
            Log.d(TAG, "Removing Firestore snapshot listener for user: $uid")
            listener?.remove()
        }
    }

    override suspend fun createOrUpdateProfileFromAuth(user: FirebaseUser): Result<UserProfile> {
        return try {
            val uid = user.uid
            val userDocRef = db.collection("users").document(uid)
            val existingDoc = userDocRef.get().await()

            if (existingDoc.exists()) {
                val existingProfile = UserProfile.fromDocument(existingDoc)
                if (existingProfile != null) {
                    // Preserve existing user fields, only update photoUrl or email if missing
                    val updates = mutableMapOf<String, Any>()
                    if (existingProfile.photoUrl.isNullOrBlank() && user.photoUrl != null) {
                        updates["photoUrl"] = user.photoUrl.toString()
                    }
                    if (existingProfile.displayName.isBlank() && !user.displayName.isNullOrBlank()) {
                        updates["displayName"] = user.displayName!!
                    }
                    if (updates.isNotEmpty()) {
                        updates["updatedAt"] = FieldValue.serverTimestamp()
                        userDocRef.set(updates, SetOptions.merge()).await()
                    }
                    return Result.success(existingProfile)
                }
            }

            // Create new profile document
            val initialDisplayName = user.displayName?.takeIf { it.isNotBlank() }
                ?: user.email?.substringBefore('@')?.replaceFirstChar { it.uppercase() }
                ?: "Warrior"

            val initialUsername = "bgsm_${uid.take(6).lowercase()}"

            val newProfile = UserProfile(
                uid = uid,
                displayName = initialDisplayName,
                username = initialUsername,
                email = user.email ?: "",
                photoUrl = user.photoUrl?.toString(),
                bio = "Ready for the battleground.",
                interests = listOf("Gaming", "Clans", "Ranked Matches"),
                activeClanId = null
            )

            val writeMap = newProfile.toWriteMap(isNew = true)
            userDocRef.set(writeMap).await()
            Log.i(TAG, "Successfully created Firestore profile for UID: $uid")
            Result.success(newProfile)
        } catch (e: Exception) {
            val msg = FirebaseErrorHandler.handleFirestoreError(e, OperationType.WRITE, "users/${user.uid}")
            Result.failure(Exception(msg, e))
        }
    }

    override suspend fun updateUserProfile(uid: String, updates: Map<String, Any?>): Result<Unit> {
        return try {
            val authenticatedUid = requireCurrentUserId()
            if (authenticatedUid != uid) {
                throw SecurityException("Cannot modify another user's profile.")
            }

            val filteredUpdates = updates.toMutableMap()
            filteredUpdates["updatedAt"] = FieldValue.serverTimestamp()
            val cleanMap = filteredUpdates.mapNotNull { (k, v) ->
                if (v != null) k to v else null
            }.toMap()

            db.collection("users").document(uid).set(cleanMap, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            val msg = FirebaseErrorHandler.handleFirestoreError(e, OperationType.UPDATE, "users/$uid")
            Result.failure(Exception(msg, e))
        }
    }
}
