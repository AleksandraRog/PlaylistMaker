package com.example.playlistmaker.common.presentation

import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.search.domain.model.HistoryQueue

interface TrackListUiState : ListUiState.ListUiIncludeState<Track> {


    data object EmptyHistory : TrackListUiState

    data object LinkError : TrackListUiState

    data class History(val tracks: HistoryQueue) : TrackListUiState
}
