package com.example.playlistmaker.common.presentation

import com.example.playlistmaker.search.domain.model.HistoryQueue

sealed interface TracksState : UiState.UiIncludeState {


    data object EmptyHistory : TracksState

    data object LinkError : TracksState

    data class History(val tracks: HistoryQueue) : TracksState
}
