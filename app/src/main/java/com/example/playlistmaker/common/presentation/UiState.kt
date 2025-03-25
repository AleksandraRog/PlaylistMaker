package com.example.playlistmaker.common.presentation

import com.example.playlistmaker.common.domain.model.Track

sealed interface UiState {

    data object Loading : UiState

    data object Empty : UiState

    data object Default : UiState

    data class Content(val tracks: List<Track>) : UiState

    data class AnyTrack(val trackId: Int) : UiState

    sealed interface UiIncludeState : UiState


}