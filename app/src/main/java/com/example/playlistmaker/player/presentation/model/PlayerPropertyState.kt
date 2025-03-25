package com.example.playlistmaker.player.presentation.model

import com.example.playlistmaker.common.domain.model.Track

data class PlayerPropertyState(val track: Track, var playerState: PlayerState = PlayerState.STATE_DEFAULT, var timer : Long = 0L, var isFavorite: Boolean = false,)
