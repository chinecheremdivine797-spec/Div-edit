package com.example.auth

import android.app.Activity
import android.content.Context
import com.example.model.UserAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Local studio session manager. No Google authentication is performed. */
class GoogleAuthManager(@Suppress("UNUSED_PARAMETER") private val context: Context) {
    private val localAccount = UserAccount(
        userId = "usr_divstudio",
        displayName = "DIV Studio",
        email = "Local Studio Session",
        tier = "FREE // CINEMA STUDIO",
        isSignedIn = true
    )

    private val _currentUser = MutableStateFlow<UserAccount?>(localAccount)
    val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    /** Compatibility method retained for the existing ViewModel; it never opens Google authentication. */
    suspend fun signInWithGoogle(@Suppress("UNUSED_PARAMETER") activity: Activity): Result<UserAccount> {
        _currentUser.value = localAccount
        _authError.value = null
        return Result.success(localAccount)
    }

    /** Compatibility method retained for the existing ViewModel; it only resets the local session. */
    suspend fun signOut(@Suppress("UNUSED_PARAMETER") activity: Activity? = null) {
        _currentUser.value = localAccount
        _authError.value = null
    }

    fun signInAs(email: String, name: String) {
        _currentUser.value = UserAccount(
            userId = "usr_local",
            displayName = name,
            email = email,
            tier = "FREE // CINEMA STUDIO",
            isSignedIn = true
        )
    }
}
