package com.example.playlistmaker.common.domain.repsitory

import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface DbElementKeyRepository {

    fun insertTrackToPlaylist(track: Track, playlist: Playlist) : Flow<Boolean>
    suspend fun getTrackByIdInPlaylist(trackId: Int ) : Boolean
    fun del() : Flow<Boolean>
}
