package com.example.playlistmaker.common.domain.repsitory

import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.common.domain.model.Playlist
import kotlinx.coroutines.flow.Flow
import java.util.LinkedList

interface DbPlaylistTableRepository {

    suspend fun notifyDatabaseChanged()
    fun insertPlaylist(playlist: Playlist): Flow<Playlist>
    fun delPlaylist(playlist: Playlist): Flow<Boolean>
    fun loadPlaylistsFlow(): Flow<ConsumerData<LinkedList<Playlist>>>
    fun loadPlaylistsNotContainingTrack(trackId: Int): Flow<ConsumerData<LinkedList<Playlist>>>
    fun loadPlaylistsContainingTrack(trackId: Int): Flow<ConsumerData<LinkedList<Playlist>>>
    fun getPlaylist(id: Int): Flow<Playlist>
    fun updatePlaylist(playlist: Playlist): Flow<Playlist>
}
