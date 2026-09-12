package com.example.auth

import android.content.Context
import com.example.model.UserAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Local studio session manager. Google authentication is removed from DIV EDIT AI. */
class GoogleAuthManager(@Suppress("UNUSED_PARAMETER") private val context: Context) {
    private val _currentUser = MutableStateFlow<UserAccount?>(
        UserAccount(
            userId = "usr_divstudio",
            displayName = "DIV Studio",
            email = "Local Studio Session",
            tier = "FREE // CINEMA STUDIO",
            isSignedIn = true
        )
    )
    val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    fun signInAs(email: String, name: String) {
        _currentUser.value = UserAccount(
            userId = "usr_local",
            displayName = name,
            email = email,
            tier = "FREE // CINEMA STUDIO",
            isSignedIn = true
        )
    }

    fun clearSession() {
        _currentUser.value = UserAccount(
            userId = "usr_divstudio",
            displayName = "DIV Studio",
            email = "Local Studio Session",
            tier = "FREE // CINEMA STUDIO",
            isSignedIn = true
        )
        _authError.value = null
    }
}
