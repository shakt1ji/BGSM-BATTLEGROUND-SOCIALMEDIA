package com.bgsm.app.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bgsm.app.R
import com.bgsm.app.data.firebase.FirebaseErrorHandler
import com.bgsm.app.data.repository.AuthRepository
import com.bgsm.app.data.repository.AuthRepositoryImpl
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    object Idle : AuthUiState
    data class Loading(val message: String = "Processing...") : AuthUiState
    data class Success(val user: FirebaseUser, val message: String = "Authentication successful") : AuthUiState
    data class Error(val message: String) : AuthUiState
    data class PasswordResetSent(val email: String) : AuthUiState
}

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    private val TAG = "[com.bgsm.app] AuthViewModel"

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val currentUser: FirebaseUser?
        get() = authRepository.currentUser

    val authStateFlow = authRepository.authStateFlow

    fun clearState() {
        _uiState.value = AuthUiState.Idle
    }

    fun register(
        displayName: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        val trimmedName = displayName.trim()
        val trimmedEmail = email.trim()

        if (trimmedName.isBlank()) {
            _uiState.value = AuthUiState.Error("Display name is required.")
            return
        }
        if (trimmedEmail.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            _uiState.value = AuthUiState.Error("Please enter a valid email address.")
            return
        }
        if (password.length < 6) {
            _uiState.value = AuthUiState.Error("Password must be at least 6 characters.")
            return
        }
        if (password != confirmPassword) {
            _uiState.value = AuthUiState.Error("Passwords do not match.")
            return
        }

        _uiState.value = AuthUiState.Loading("Creating your BGSM warrior account...")
        viewModelScope.launch {
            val result = authRepository.registerWithEmail(trimmedName, trimmedEmail, password)
            result.fold(
                onSuccess = { user ->
                    _uiState.value = AuthUiState.Success(user, "Welcome to BGSM, ${user.displayName ?: trimmedName}!")
                },
                onFailure = { error ->
                    _uiState.value = AuthUiState.Error(error.message ?: "Registration failed.")
                }
            )
        }
    }

    fun login(email: String, password: String) {
        val trimmedEmail = email.trim()

        if (trimmedEmail.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            _uiState.value = AuthUiState.Error("Please enter a valid email address.")
            return
        }
        if (password.isBlank()) {
            _uiState.value = AuthUiState.Error("Password is required.")
            return
        }

        _uiState.value = AuthUiState.Loading("Authenticating credentials...")
        viewModelScope.launch {
            val result = authRepository.loginWithEmail(trimmedEmail, password)
            result.fold(
                onSuccess = { user ->
                    _uiState.value = AuthUiState.Success(user, "Welcome back!")
                },
                onFailure = { error ->
                    _uiState.value = AuthUiState.Error(error.message ?: "Sign in failed.")
                }
            )
        }
    }

    fun sendPasswordReset(email: String) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            _uiState.value = AuthUiState.Error("Please enter a valid email address.")
            return
        }

        _uiState.value = AuthUiState.Loading("Sending password reset email...")
        viewModelScope.launch {
            val result = authRepository.sendPasswordReset(trimmedEmail)
            result.fold(
                onSuccess = {
                    _uiState.value = AuthUiState.PasswordResetSent(trimmedEmail)
                },
                onFailure = { error ->
                    _uiState.value = AuthUiState.Error(error.message ?: "Failed to send reset email.")
                }
            )
        }
    }

    fun signInWithGoogle(activity: Activity) {
        _uiState.value = AuthUiState.Loading("Connecting to Google...")
        val credentialManager = CredentialManager.create(activity)

        val clientId = try {
            activity.getString(R.string.default_web_client_id)
        } catch (e: Exception) {
            Log.e(TAG, "Google default_web_client_id resource not found", e)
            _uiState.value = AuthUiState.Error(
                "Google Sign-In Web Client ID is not configured. Please verify google-services.json."
            )
            return
        }

        val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInOption)
            .build()

        viewModelScope.launch {
            try {
                val result = credentialManager.getCredential(activity, request)
                val credential = result.credential

                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                    val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)

                    val authResult = authRepository.signInWithCredential(authCredential)
                    authResult.fold(
                        onSuccess = { user ->
                            _uiState.value = AuthUiState.Success(user, "Signed in as ${user.displayName ?: user.email}")
                        },
                        onFailure = { error ->
                            _uiState.value = AuthUiState.Error(error.message ?: "Google Authentication failed.")
                        }
                    )
                } else {
                    _uiState.value = AuthUiState.Error("Received unsupported credential type.")
                }
            } catch (e: Exception) {
                if (e is GetCredentialCancellationException) {
                    _uiState.value = AuthUiState.Idle
                } else {
                    Log.e(TAG, "Credential Manager error during Google Sign-In", e)
                    val friendly = FirebaseErrorHandler.getFriendlyErrorMessage(e)
                    _uiState.value = AuthUiState.Error(friendly)
                }
            }
        }
    }

    fun signOut(context: Context) {
        _uiState.value = AuthUiState.Loading("Signing out...")
        viewModelScope.launch {
            val result = authRepository.signOut(context)
            result.fold(
                onSuccess = {
                    _uiState.value = AuthUiState.Idle
                },
                onFailure = { error ->
                    _uiState.value = AuthUiState.Error(error.message ?: "Sign out encountered an error.")
                }
            )
        }
    }
}
