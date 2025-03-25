package com.example.playlistmaker.player.domain.interactors

import android.os.Bundle
import com.example.playlistmaker.common.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackInteractor {

    fun favoriteControl(track: Track, isFavorite: Boolean) : Flow<Boolean>

    fun loadTrackFlow(trackIdBundle: Bundle): Flow<Pair<Track, Int>>
}
