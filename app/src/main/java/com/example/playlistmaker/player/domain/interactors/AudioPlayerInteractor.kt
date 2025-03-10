package com.example.playlistmaker.player.domain.interactors

import com.example.playlistmaker.player.presentation.model.PlayerState
import kotlinx.coroutines.flow.Flow

interface AudioPlayerInteractor {

    fun currentPosition() : Long

    fun prepareFlow(url: String,) : Flow<PlayerState>

    fun playFlow() : Flow<PlayerState>

    fun stopFlow() : Flow<PlayerState>

    fun releaseFlow() : Flow<PlayerState>
}
