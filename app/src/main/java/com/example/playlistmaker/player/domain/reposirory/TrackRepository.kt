package com.example.playlistmaker.player.domain.reposirory

import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.domain.model.Track
import kotlinx.coroutines.flow.Flow
import java.util.LinkedList

interface TrackRepository {

    fun insertTrack(track: Track): Flow<Boolean>
    suspend fun getTrackByIdInPlaylist(trackId: Int,): Boolean
    fun delTrack(track: Track): Flow<Boolean>
    fun getTrackByIdFlow(trackId: Int): Flow<ConsumerData<Track>>
    fun insertTrackToPlaylist(track: Track, playlist: Playlist): Flow<Boolean>
    fun loadPlaylistsContainingTrack(trackId: Int): Flow<ConsumerData<LinkedList<Playlist>>>
    fun loadPlaylistsNotContainingTrack(trackId: Int): Flow<ConsumerData<LinkedList<Playlist>>>

}
