package com.example.snailmesssenger.feature.media

import androidx.compose.runtime.Immutable

enum class MediaKind { PHOTO, VIDEO }

@Immutable
data class MediaAsset(
    val id: String,
    val uri: String? = null,
    val kind: MediaKind = MediaKind.PHOTO,
    val durationLabel: String? = null,
    val title: String,
    val tintArgb: Long,
)

@Immutable
data class SelectedMediaState(
    val selectedIds: List<String> = emptyList(),
    val caption: String = "",
    val highQuality: Boolean = false,
    val selfDestructSeconds: Int? = null,
)

@Immutable
data class AttachmentUiState(
    val media: List<MediaAsset> = emptyList(),
    val selection: SelectedMediaState = SelectedMediaState(),
    val activeShortcut: AttachmentShortcut = AttachmentShortcut.GALLERY,
    val isSheetVisible: Boolean = false,
    val isPreviewVisible: Boolean = false,
    val isSending: Boolean = false,
    val lastSentSummary: String? = null,
)

enum class AttachmentShortcut { GALLERY, FILE, LOCATION, CONTACT, AUDIO_POLL }
