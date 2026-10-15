package com.example.snailmesssenger.feature.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ChatTab(val name: String, val unreadOnly: Boolean = false)

data class SettingsUiState(
    val chatTextSize: Float = 16f,
    val bubbleCornerRadius: Float = 10f,
    val autoNightMode: Boolean = false,
    val themePalette: String = "Default Blue",
    val fingerprintLock: Boolean = false,
    val privateChatNotifications: Boolean = true,
    val groupNotifications: Boolean = true,
    val includeMutedChats: Boolean = false,
    val countUnreadMessages: Boolean = true,
    val mobileDataDownloads: Boolean = true,
    val wifiDownloads: Boolean = true,
    val isClearingCache: Boolean = false,
    val cacheProgress: Float = 0f,
    val blockedUsersCount: Int = 0,
    val twoStepEnabled: Boolean = false,
    val tabs: List<ChatTab> = listOf(ChatTab("Personal"), ChatTab("Work"), ChatTab("Unread", unreadOnly = true)),
)

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setChatTextSize(value: Float) = _uiState.update { it.copy(chatTextSize = value) }
    fun setBubbleCornerRadius(value: Float) = _uiState.update { it.copy(bubbleCornerRadius = value) }
    fun setAutoNightMode(enabled: Boolean) = _uiState.update { it.copy(autoNightMode = enabled) }
    fun setThemePalette(palette: String) = _uiState.update { it.copy(themePalette = palette) }
    fun setFingerprintLock(enabled: Boolean) = _uiState.update { it.copy(fingerprintLock = enabled) }
    fun setPrivateChatNotifications(enabled: Boolean) = _uiState.update { it.copy(privateChatNotifications = enabled) }
    fun setGroupNotifications(enabled: Boolean) = _uiState.update { it.copy(groupNotifications = enabled) }
    fun setIncludeMutedChats(enabled: Boolean) = _uiState.update { it.copy(includeMutedChats = enabled) }
    fun setCountUnreadMessages(enabled: Boolean) = _uiState.update { it.copy(countUnreadMessages = enabled) }
    fun setMobileDataDownloads(enabled: Boolean) = _uiState.update { it.copy(mobileDataDownloads = enabled) }
    fun setWifiDownloads(enabled: Boolean) = _uiState.update { it.copy(wifiDownloads = enabled) }
    fun setTwoStepEnabled(enabled: Boolean) = _uiState.update { it.copy(twoStepEnabled = enabled) }

    fun createTab(name: String) {
        val clean = name.trim()
        if (clean.isEmpty() || _uiState.value.tabs.any { it.name.equals(clean, ignoreCase = true) }) return
        _uiState.update { it.copy(tabs = it.tabs + ChatTab(clean)) }
    }

    fun removeTab(name: String) {
        _uiState.update { state -> state.copy(tabs = state.tabs.filterNot { it.name == name }) }
    }

    fun startClearCache() {
        _uiState.update { it.copy(isClearingCache = true, cacheProgress = 0.15f) }
        _uiState.update { it.copy(cacheProgress = 0.55f) }
        _uiState.update { it.copy(cacheProgress = 1f, isClearingCache = false) }
    }
}
