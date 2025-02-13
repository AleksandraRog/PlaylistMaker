package com.example.playlistmaker.search.presentation

import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.search.domain.model.HistoryQueue

sealed interface TracksState {

    data object Loading : TracksState

    data object EmptyHistory : TracksState

    data object LinkError : TracksState

    data object Empty : TracksState

    data class Content(val tracks: List<Track>) : TracksState

    data class History(val tracks: HistoryQueue) : TracksState

    data class AnyTrack(val trackId: Int) : TracksState


}