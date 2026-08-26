package com.bgsm.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bgsm.app.data.model.UserProfile
import com.bgsm.app.data.repository.UserRepository
import com.bgsm.app.data.repository.UserRepositoryImpl
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed interface ProfileUiState {
    object Loading : ProfileUiState
    data class Success(val profile: UserProfile) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

class ProfileViewModel(
    private val userRepository: UserRepository = UserRepositoryImpl()
) : ViewModel() {

    private val TAG = "[com.bgsm.app] ProfileViewModel"
    private val auth = Firebase.auth

    private val _profileState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val profileState: StateFlow<ProfileUiState> = _profileState.asStateFlow()

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating.asStateFlow()

    private var profileJob: Job? = null

    init {
        observeCurrentProfile()
    }

    fun observeCurrentProfile() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            _profileState.value = ProfileUiState.Error("No authenticated session.")
            return
        }

        profileJob?.cancel()
        profileJob = viewModelScope.launch {
            _profileState.value = ProfileUiState.Loading
            userRepository.observeUserProfile(uid)
                .catch { exception ->
                    Log.e(TAG, "Error observing user profile", exception)
                    _profileState.value = ProfileUiState.Error(exception.localizedMessage ?: "Failed to sync profile.")
                }
                .collect { profile ->
                    if (profile != null) {
                        _profileState.value = ProfileUiState.Success(profile)
                    } else {
                        // Attempt to populate if missing
                        auth.currentUser?.let { user ->
                            userRepository.createOrUpdateProfileFromAuth(user)
                        }
                    }
                }
        }
    }

    fun updateProfile(
        displayName: String,
        username: String,
        bio: String,
        interests: List<String>
    ) {
        val uid = auth.currentUser?.uid ?: return
        _isUpdating.value = true

        viewModelScope.launch {
            val updates = mapOf(
                "displayName" to displayName.trim(),
                "username" to username.trim().lowercase(),
                "bio" to bio.trim(),
                "interests" to interests
            )

            val result = userRepository.updateUserProfile(uid, updates)
            _isUpdating.value = false
            if (result.isFailure) {
                Log.e(TAG, "Failed to update profile", result.exceptionOrNull())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        profileJob?.cancel()
    }
}
