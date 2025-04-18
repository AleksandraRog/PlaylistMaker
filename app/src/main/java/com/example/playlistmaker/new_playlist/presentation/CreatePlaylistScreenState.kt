package com.example.playlistmaker.new_playlist.presentation

import android.net.Uri
import com.example.playlistmaker.common.domain.model.Playlist

sealed interface CreatePlaylistScreenState {

    data class ActivateCropper(val uri: Uri) : CreatePlaylistScreenState
    data class InstallLogo(val url: String) : CreatePlaylistScreenState
    data class SavePlaylist(val newPlaylist : Playlist): CreatePlaylistScreenState
    data class ClosePlaylist(val fl : Boolean): CreatePlaylistScreenState
}
