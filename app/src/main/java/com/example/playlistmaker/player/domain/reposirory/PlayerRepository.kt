package com.example.playlistmaker.player.domain.reposirory

import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.player.presentation.model.PlayerState
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {

    fun currentPosition() : ConsumerData<Long>

    fun preparePlayerFlow(url: String,) : Flow<PlayerState>

    fun playPlayerFlow() : Flow<PlayerState>

    fun pausePlayerFlow() : Flow<PlayerState>

    fun releasePlayerFlow() : Flow<PlayerState>
}
