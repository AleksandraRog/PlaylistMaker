package com.example.playlistmaker.playlist.presentation

import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.presentation.ListUiState
import com.example.playlistmaker.common.presentation.model.SharingObjects

interface PlaylistUiState : ListUiState.ListUiIncludeState<Track> {

    object DeletePlaylist: PlaylistUiState
    object ShowToastEmpty: PlaylistUiState
    object DeleteTrack: PlaylistUiState
    data class LoadPlaylist(val playlistModel: Playlist): PlaylistUiState
    data class SharePlaylist(val sharingObj: SharingObjects) : PlaylistUiState
    data class ExpandBottomSheetMenu(val playlist: Playlist) : PlaylistUiState
    data class EditPlaylist(val playlist: Playlist) : PlaylistUiState

}
