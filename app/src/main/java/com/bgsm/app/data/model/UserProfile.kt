package com.bgsm.app.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue

/**
 * BGSM UserProfile Data Model.
 * Base fields establish permanent user identity and Clan association foundation.
 * Follows strict Firestore requirements:
 * - Default values for synthetic no-arg constructor
 * - Nullable Timestamp fields
 * - Null-filtering in toMap() to satisfy security rules
 */
data class UserProfile(
    val uid: String = "",
    val displayName: String = "",
    val username: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val bio: String = "",
    val interests: List<String> = emptyList(),
    val activeClanId: String? = null,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val joinedPlatformAt: Timestamp? = null
) {
    /**
     * Converts to Firestore write payload with server timestamps and null filtering.
     */
    fun toWriteMap(isNew: Boolean = false): Map<String, Any> {
        val baseMap = mutableMapOf<String, Any?>()
        baseMap["uid"] = uid
        baseMap["displayName"] = displayName
        baseMap["username"] = if (username.isNotBlank()) username else "user_${uid.take(6)}"
        baseMap["email"] = email
        if (photoUrl != null && photoUrl.isNotBlank()) {
            baseMap["photoUrl"] = photoUrl
        }
        baseMap["bio"] = bio
        baseMap["interests"] = interests
        if (activeClanId != null && activeClanId.isNotBlank()) {
            baseMap["activeClanId"] = activeClanId
        }

        if (isNew) {
            baseMap["createdAt"] = FieldValue.serverTimestamp()
            baseMap["joinedPlatformAt"] = FieldValue.serverTimestamp()
        }
        baseMap["updatedAt"] = FieldValue.serverTimestamp()

        return baseMap.mapNotNull { (key, value) ->
            if (value != null) key to value else null
        }.toMap()
    }

    companion object {
        fun fromDocument(doc: DocumentSnapshot): UserProfile? {
            if (!doc.exists()) return null
            val data = doc.data ?: return null

            @Suppress("UNCHECKED_CAST")
            val rawInterests = data["interests"] as? List<*>
            val interestsList = rawInterests?.filterIsInstance<String>() ?: emptyList()

            return UserProfile(
                uid = doc.getString("uid") ?: doc.id,
                displayName = doc.getString("displayName") ?: "",
                username = doc.getString("username") ?: "",
                email = doc.getString("email") ?: "",
                photoUrl = doc.getString("photoUrl"),
                bio = doc.getString("bio") ?: "",
                interests = interestsList,
                activeClanId = doc.getString("activeClanId"),
                createdAt = doc.getTimestamp("createdAt"),
                updatedAt = doc.getTimestamp("updatedAt"),
                joinedPlatformAt = doc.getTimestamp("joinedPlatformAt")
            )
        }
    }
}
