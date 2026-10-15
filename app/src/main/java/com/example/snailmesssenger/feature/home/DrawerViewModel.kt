package com.example.snailmesssenger.feature.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


data class DrawerAccount(
    val name: String,
    val username: String,
    val initials: String,
    val isActive: Boolean = false,
)

data class DrawerUiState(
    val accountSwitcherExpanded: Boolean = false,
    val accounts: List<DrawerAccount> = listOf(
        DrawerAccount("Nayan", "@nayan", "N", true),
    ),
    val showHelp: Boolean = false,
    val showNewGroup: Boolean = false,
)

class DrawerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DrawerUiState())
    val uiState: StateFlow<DrawerUiState> = _uiState.asStateFlow()

    fun toggleAccountSwitcher() {
        _uiState.value = _uiState.value.copy(
            accountSwitcherExpanded = !_uiState.value.accountSwitcherExpanded,
        )
    }

    fun selectAccount(index: Int) {
        val updated = _uiState.value.accounts.mapIndexed { i, account ->
            account.copy(isActive = i == index)
        }
        _uiState.value = _uiState.value.copy(
            accounts = updated,
            accountSwitcherExpanded = false,
        )
    }

    fun showHelp() {
        _uiState.value = _uiState.value.copy(showHelp = true)
    }

    fun dismissHelp() {
        _uiState.value = _uiState.value.copy(showHelp = false)
    }

    fun showNewGroup() {
        _uiState.value = _uiState.value.copy(showNewGroup = true)
    }

    fun dismissNewGroup() {
        _uiState.value = _uiState.value.copy(showNewGroup = false)
    }
}
