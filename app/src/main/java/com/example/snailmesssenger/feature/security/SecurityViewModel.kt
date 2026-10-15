package com.example.snailmesssenger.feature.security

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Local-only security state. No secrets leave the device in this prototype. */
data class SecurityUiState(
    val username: String = "",
    val password: String = "",
    val usernameValid: Boolean = false,
    val passwordValid: Boolean = false,
    val twoStepEnabled: Boolean = false,
    val pinConfigured: Boolean = false,
    val mascotHidden: Boolean = false,
    val pendingTwoStep: Boolean = false,
    val error: String? = null,
    val resetCodeVisible: Boolean = false,
    val generatedResetCode: String? = null,
)

class SecurityViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private val _uiState = MutableStateFlow(
        SecurityUiState(
            twoStepEnabled = prefs.getBoolean(KEY_2FA_ENABLED, false),
            pinConfigured = prefs.getString(KEY_2FA_PIN, null) != null,
        ),
    )
    val uiState: StateFlow<SecurityUiState> = _uiState.asStateFlow()

    fun updateUsername(value: String) {
        _uiState.update { it.copy(username = value, usernameValid = value.trim().length >= 3, error = null) }
    }

    fun updatePassword(value: String, visible: Boolean) {
        _uiState.update {
            it.copy(
                password = value,
                passwordValid = value.length >= 6,
                mascotHidden = value.isNotEmpty() && !visible,
                error = null,
            )
        }
    }

    fun setPasswordVisibility(visible: Boolean) {
        _uiState.update { it.copy(mascotHidden = it.password.isNotEmpty() && !visible) }
    }

    fun validateCredentials(): Boolean {
        val state = _uiState.value
        val usernameValid = state.username.trim().length >= 3
        val passwordValid = state.password.length >= 6
        _uiState.update {
            it.copy(
                usernameValid = usernameValid,
                passwordValid = passwordValid,
                error = when {
                    !usernameValid -> "Username must contain at least 3 characters."
                    !passwordValid -> "Password must contain at least 6 characters."
                    else -> null
                },
            )
        }
        return usernameValid && passwordValid
    }

    fun setTwoStepPin(pin: String): Boolean {
        if (pin.length < 4 || pin.length > 8 || !pin.all(Char::isDigit)) return false
        prefs.edit().putString(KEY_2FA_PIN, pin).putBoolean(KEY_2FA_ENABLED, true).apply()
        _uiState.update { it.copy(twoStepEnabled = true, pinConfigured = true, error = null) }
        return true
    }

    fun disableTwoStep() {
        prefs.edit().remove(KEY_2FA_PIN).putBoolean(KEY_2FA_ENABLED, false).apply()
        _uiState.update { it.copy(twoStepEnabled = false, pinConfigured = false, pendingTwoStep = false) }
    }

    fun refreshTwoStepState() {
        _uiState.update {
            it.copy(
                twoStepEnabled = prefs.getBoolean(KEY_2FA_ENABLED, false),
                pinConfigured = prefs.getString(KEY_2FA_PIN, null) != null,
            )
        }
    }

    fun beginTwoStepVerification() {
        refreshTwoStepState()
        _uiState.update { it.copy(pendingTwoStep = true, error = null) }
    }

    fun verifyPin(pin: String): Boolean {
        val expected = prefs.getString(KEY_2FA_PIN, null)
        val valid = expected != null && pin == expected
        _uiState.update { it.copy(error = if (valid) null else "Incorrect verification PIN.") }
        return valid
    }

    fun requestLocalResetCode() {
        val code = (100000..999999).random().toString()
        prefs.edit().putString(KEY_RESET_CODE, code).apply()
        _uiState.update { it.copy(resetCodeVisible = true, generatedResetCode = code, error = null) }
    }

    fun resetWithCode(code: String, newPin: String): Boolean {
        val stored = prefs.getString(KEY_RESET_CODE, null)
        if (stored == null || code != stored) {
            _uiState.update { it.copy(error = "Invalid local recovery code.") }
            return false
        }
        if (!setTwoStepPin(newPin)) {
            _uiState.update { it.copy(error = "PIN must contain 4–8 digits.") }
            return false
        }
        prefs.edit().remove(KEY_RESET_CODE).apply()
        _uiState.update { it.copy(resetCodeVisible = false, generatedResetCode = null, pendingTwoStep = false, error = null) }
        return true
    }

    companion object {
        private const val PREFS = "snail_security"
        private const val KEY_2FA_ENABLED = "two_step_enabled"
        private const val KEY_2FA_PIN = "two_step_pin"
        private const val KEY_RESET_CODE = "two_step_reset_code"
    }
}
