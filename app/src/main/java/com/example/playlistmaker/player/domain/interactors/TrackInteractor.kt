package com.example.playlistmaker.player.domain.interactors

import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.presentation.model.ItemPlaylistWrapper
import com.example.playlistmaker.common.presentation.model.ExtraActionBundleKey
import kotlinx.coroutines.flow.Flow
import java.util.LinkedList

interface TrackInteractor {

    suspend fun favoriteControl(track: Track, isFavorite: Boolean) : Flow<Boolean>
    fun loadTrackFlow(extraActionBundleKey: ExtraActionBundleKey?): Flow<Pair<Track?, Int>>
    fun loadPlaylists(track: Track,) : Flow<LinkedList<Pair<Playlist,Boolean>>>
    fun insertTrackToPlaylist(track: Track, playlist: ItemPlaylistWrapper.PlaylistPair): Flow<String>

}
