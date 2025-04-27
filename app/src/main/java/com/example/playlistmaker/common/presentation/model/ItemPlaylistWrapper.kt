package com.example.playlistmaker.common.presentation.model

import com.example.playlistmaker.common.domain.model.Playlist

sealed class ItemPlaylistWrapper {
    abstract val playlist: Playlist

    class PlaylistSingle(override val playlist: Playlist) : ItemPlaylistWrapper()
    open class PlaylistPair(override val playlist: Playlist, val isOnPlaylist: Boolean) : ItemPlaylistWrapper()

}
