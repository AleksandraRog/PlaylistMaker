package com.example.playlistmaker.playlist.domain.repository

import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.domain.model.Track
import kotlinx.coroutines.flow.Flow
import java.util.LinkedList

interface PlaylistRepository {

    fun getPlaylistByIdFlow(id: Int): Flow<Playlist>
    fun getTrackListInPlaylist(id: Int): Flow<ConsumerData<LinkedList<Track>>>
    fun deletePlaylist(playlist: Playlist): Flow<Boolean>
    fun deleteTrackFromPlaylist(track: Track, playlist: Playlist): Flow<Boolean>
}
