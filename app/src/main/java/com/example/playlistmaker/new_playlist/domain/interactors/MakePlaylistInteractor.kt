package com.example.playlistmaker.new_playlist.domain.interactors

import com.example.playlistmaker.common.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface MakePlaylistInteractor {

    fun getImageAspectRatio(uri: String): Flow<Float>
    fun saveUriToFile(uri: String): Flow<String>
    fun createNewPlaylist(playlist: Playlist) :Flow<Playlist>
    fun saveBitmapToFile(croppedByteArray: ByteArray): Flow<String>
    fun getImagesFromStorage(): Flow<List<String>>
    fun deleteFile(fileUrl: String): Flow<Boolean>
}
