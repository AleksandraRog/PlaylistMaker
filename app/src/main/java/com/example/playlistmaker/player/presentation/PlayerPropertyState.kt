package com.example.playlistmaker.player.presentation

import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.player.presentation.model.PlayerState

data class PlayerPropertyState(val track: Track, var playerState: PlayerState = PlayerState.STATE_DEFAULT, var timer : Long = 0L,)
