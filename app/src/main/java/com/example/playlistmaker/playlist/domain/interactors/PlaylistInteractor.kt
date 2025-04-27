package com.example.playlistmaker.playlist.domain.interactors

import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.presentation.model.SharingObjects
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor  {

    fun loadPlaylistFlow(playlistId: Int): Flow<Pair<Playlist?, List<Track>?>>
    fun getIntentProperty(sharingObjects: SharingObjects, playlist: Playlist): Flow<SharingObjects>
    fun getSharePlaylistLink(playlist: Playlist) : Flow<String>
    fun deletePlaylist(playlist: Playlist): Flow<Boolean>
    fun deleteTrackFromPlaylist(track: Track, playlist: Playlist): Flow<Boolean>
}
