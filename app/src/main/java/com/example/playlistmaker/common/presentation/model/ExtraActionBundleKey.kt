package com.example.playlistmaker.common.presentation.model

enum class ExtraActionBundleKey(val value: String, var id: Int = 0) {
    TRACK_EXTRA_HISTORY("TRACK_EXTRA_HISTORY"),
    TRACK_EXTRA_FAVORITE("TRACK_EXTRA_FAVORITE"),
    PLAYLIST_EXTRA("PLAYLIST_EXTRA");
}
