package com.example.playlistmaker.player.domain.interactors.impl

import com.example.playlistmaker.common.domain.repsitory.DbRepository
import com.example.playlistmaker.player.domain.interactors.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.reposirory.PlayerRepository
import com.example.playlistmaker.player.presentation.model.PlayerState
import kotlinx.coroutines.flow.Flow

class AudioPlayerInteractorImpl(
    private val repository: PlayerRepository,): AudioPlayerInteractor {

    override fun prepareFlow(url: String): Flow<PlayerState> {
        return repository.preparePlayerFlow(url)

    }

    override fun playFlow(): Flow<PlayerState> {
        return repository.playPlayerFlow()
    }

    override fun stopFlow(): Flow<PlayerState> {
        return repository.pausePlayerFlow()
    }

    override fun releaseFlow(): Flow<PlayerState> {
        return repository.releasePlayerFlow()
    }

    override fun currentPosition() : Long {
        return  repository.currentPosition().result
    }
}
