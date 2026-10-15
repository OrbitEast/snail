package com.example.snailmesssenger.feature.media

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MockMediaRepository {
    suspend fun loadGallery(): List<MediaAsset> = withContext(Dispatchers.IO) {
        delay(120)
        val colors = listOf(0xFF5C7AEA, 0xFFED9B40, 0xFF4DAD91, 0xFFB26DD2, 0xFFDB6D82, 0xFF468FAF, 0xFFBEA34A, 0xFF6C8D64, 0xFF9A79C7, 0xFFCB7952, 0xFF5588B8, 0xFF7DA7A0)
        colors.mapIndexed { index, color ->
            val video = index == 3 || index == 8
            MediaAsset(id = "mock-media-$index", kind = if (video) MediaKind.VIDEO else MediaKind.PHOTO, durationLabel = if (video) "0:12" else null, title = if (video) "Video ${index + 1}" else "Photo ${index + 1}", tintArgb = color)
        }
    }
}

class AttachmentViewModel(private val repository: MockMediaRepository = MockMediaRepository()) : ViewModel() {
    private val _uiState = MutableStateFlow(AttachmentUiState())
    val uiState = _uiState.asStateFlow()
    init { refreshGallery() }
    fun showSheet() { _uiState.update { it.copy(isSheetVisible = true, isPreviewVisible = false) } }
    fun hideSheet() { _uiState.update { it.copy(isSheetVisible = false) } }
    fun setShortcut(shortcut: AttachmentShortcut) { _uiState.update { it.copy(activeShortcut = shortcut) } }
    fun refreshGallery() { viewModelScope.launch { val media = repository.loadGallery(); _uiState.update { it.copy(media = media) } } }
    fun toggleSelection(id: String) { _uiState.update { state -> val ids = state.selection.selectedIds; state.copy(selection = state.selection.copy(selectedIds = if (id in ids) ids.filterNot { it == id } else ids + id)) } }
    fun openPreview(id: String? = null) { _uiState.update { state -> val ids = state.selection.selectedIds; val next = when { id == null -> ids; id in ids -> ids; ids.isEmpty() -> listOf(id); else -> ids + id }; state.copy(selection = state.selection.copy(selectedIds = next), isPreviewVisible = true, isSheetVisible = false) } }
    fun closePreview() { _uiState.update { it.copy(isPreviewVisible = false, isSheetVisible = true) } }
    fun updateCaption(value: String) { _uiState.update { it.copy(selection = it.selection.copy(caption = value)) } }
    fun setHighQuality(value: Boolean) { _uiState.update { it.copy(selection = it.selection.copy(highQuality = value)) } }
    fun setSelfDestruct(seconds: Int?) { _uiState.update { it.copy(selection = it.selection.copy(selfDestructSeconds = seconds)) } }
    fun sendSelected(onSent: (List<MediaAsset>, String, Boolean, Int?) -> Unit) { val state = _uiState.value; val selected = state.media.filter { it.id in state.selection.selectedIds }; if (selected.isEmpty()) return; viewModelScope.launch { _uiState.update { it.copy(isSending = true) }; withContext(Dispatchers.Default) { delay(100) }; onSent(selected, state.selection.caption, state.selection.highQuality, state.selection.selfDestructSeconds); _uiState.update { it.copy(isSending = false, isSheetVisible = false, isPreviewVisible = false, selection = SelectedMediaState(), lastSentSummary = "Sent ${selected.size} item(s)") } } }
}
