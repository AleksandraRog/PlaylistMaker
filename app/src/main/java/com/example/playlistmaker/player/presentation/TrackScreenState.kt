package com.example.playlistmaker.player.presentation

import com.example.playlistmaker.common.domain.model.Track

sealed class TrackScreenState {

    data object Loading: TrackScreenState()

    data class Content(
        val trackModel: Track,
    ): TrackScreenState()
}
