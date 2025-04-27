package com.example.playlistmaker.new_playlist.presentation

import android.net.Uri
import com.example.playlistmaker.common.domain.model.Playlist

sealed interface CreatePlaylistScreenState {

    object Empty : CreatePlaylistScreenState
    data class ActivateCropper(val uri: Uri) : CreatePlaylistScreenState
    data class InstallLogo(val url: String) : CreatePlaylistScreenState
    data class OnSavePlaylist(val newPlaylist : Playlist): CreatePlaylistScreenState
    data class LoadPlaylist(val playlist : Playlist): CreatePlaylistScreenState
    data class ClosePlaylist(val fl : Boolean): CreatePlaylistScreenState
}
