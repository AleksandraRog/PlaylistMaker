package com.example.playlistmaker.favorite_tracks.domain.interactors

import com.example.playlistmaker.common.domain.model.Track
import kotlinx.coroutines.flow.Flow
import java.util.LinkedList

interface FavoriteTracksInteractor {

    fun loadTracksFlow(): Flow<Pair<LinkedList<Track>, Int>>

}
