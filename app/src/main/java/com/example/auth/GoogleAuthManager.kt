package com.example.auth

import android.app.Activity
import com.example.model.UserAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Local authentication compatibility layer.
 *
 * Google/Firebase authentication has been removed from DIV EDIT AI.
 * This class keeps the existing ViewModel API compiling while using
 * only local/in-memory account state.
 */
class GoogleAuthManager(@Suppress("UNUSED_PARAMETER") context: android.content.Context) {
    private val _currentUser = MutableStateFlow<UserAccount?>(null)
    val currentUser: StateFlow<UserAccount?> = _currentUser

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError

    suspend fun signInWithGoogle(@Suppress("UNUSED_PARAMETER") activity: Activity): Result<UserAccount> {
        val error = IllegalStateException("Google Sign-In is disabled in this build. Use local account mode.")
        _authError.value = error.message
        return Result.failure(error)
    }

    fun signInAs(email: String, name: String) {
        val account = UserAccount(
            userId = "usr_" + email.replace("@", "_").replace(".", "_"),
            displayName = name,
            email = email,
            isSignedIn = true
        )
        _currentUser.value = account
        _authError.value = null
    }

    fun signOut(@Suppress("UNUSED_PARAMETER") activity: Activity? = null) {
        _currentUser.value = null
        _authError.value = null
    }
}
