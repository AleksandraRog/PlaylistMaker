package com.example.playlistmaker.common.presentation

import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.presentation.model.ItemPlaylistWrapper

interface TrackUiState : ListUiState.ListUiIncludeState<ItemPlaylistWrapper.PlaylistPair> {

    data class LoadTrack(val trackModel: Track,): TrackUiState
    data class ToastMessage(val message: String,): TrackUiState
}
