package com.example.playlistmaker.player.domain.reposirory

import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.common.domain.consumer.ListenerConsumer
import com.example.playlistmaker.player.presentation.model.PlayerState

interface PlayerRepository {

    fun playPlayer() : ConsumerData<PlayerState>

    fun pausePlayer() : ConsumerData<PlayerState>

    fun preparePlayer(url: String, prepareListenerConsumer: ListenerConsumer<PlayerState>, completionListenerConsumer: ListenerConsumer<PlayerState>)

    fun releasePlayer() : ConsumerData<PlayerState>

    fun currentPosition() : ConsumerData<Long>
}
