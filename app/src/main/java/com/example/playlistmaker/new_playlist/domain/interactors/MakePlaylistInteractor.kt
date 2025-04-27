package com.example.playlistmaker.new_playlist.domain.interactors

import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.presentation.model.ExtraActionBundleKey
import kotlinx.coroutines.flow.Flow

interface MakePlaylistInteractor {

    fun getImageAspectRatio(uri: String): Flow<Float>
    fun saveUriToFile(uri: String): Flow<String>
    fun createNewPlaylist(playlist: Playlist) :Flow<Playlist>
    fun saveBitmapToFile(croppedByteArray: ByteArray): Flow<String>
    fun getImagesFromStorage(): Flow<List<String>>
    fun deleteFile(fileUrl: String): Flow<Boolean>
    fun loadPlaylistFlow(playlistId: ExtraActionBundleKey?): Flow<Playlist?>
    fun processPlaylist(playlist: Playlist, editFlag: Boolean): Flow<Playlist>
}
