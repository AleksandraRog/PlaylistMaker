package com.example.playlistmaker.new_playlist.domain.repository

import com.example.playlistmaker.common.domain.model.Playlist
import kotlinx.coroutines.flow.Flow
import java.io.File

interface NewPlaylistRepository {

    // file
    fun createNewFile(fileName: String): File
    fun saveImageToPrivateStorage(uri: String, createdFile : File,): Flow<String>
    fun getImageAspectRatio(uri: String): Flow<Float>
    fun saveBitmapToFile(croppedByteArray: ByteArray, createdFile : File,): Flow<String>
    fun getImagesFromStorage(): Flow<List<String>>
    fun deleteFile(file: File): Flow<Boolean>

    // db
    fun insertPlaylist(playlist : Playlist): Flow<Playlist>

}
