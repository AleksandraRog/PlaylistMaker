package com.example.playlistmaker.domain.repsitory

import com.example.playlistmaker.domain.consumer.ConsumerData
import com.example.playlistmaker.domain.consumer.ListenerConsumer
import com.example.playlistmaker.domain.model.PlayerState

interface PlayerRepository {

    fun playPlayer() : ConsumerData<PlayerState>

    fun pausePlayer() : ConsumerData<PlayerState>

    fun preparePlayer(url: String, prepareListenerConsumer: ListenerConsumer<PlayerState>, completionListenerConsumer: ListenerConsumer<PlayerState>)
}
