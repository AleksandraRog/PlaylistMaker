package com.example.playlistmaker.new_playlist.domain.interactors.impl

import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.presentation.model.ExtraActionBundleKey
import com.example.playlistmaker.new_playlist.data.file.GetValueFileString
import com.example.playlistmaker.new_playlist.domain.interactors.MakePlaylistInteractor
import com.example.playlistmaker.new_playlist.domain.repository.NewPlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class MakePlaylistInteractorImpl(private val newPlaylistRepository: NewPlaylistRepository,
                                 private val getValueFileString: GetValueFileString,
    ) : MakePlaylistInteractor {

    private fun createNewFile(): File {

        val fileName = String.format(
            getValueFileString.fileNameTemplate,
            System.currentTimeMillis().toString()
        )
        return newPlaylistRepository.createNewFile(fileName)
    }

    override fun getImageAspectRatio(uri: String): Flow<Float> {

        return newPlaylistRepository.getImageAspectRatio(uri)
    }

    override fun saveUriToFile(uri: String): Flow<String> {

        return newPlaylistRepository.saveImageToPrivateStorage(uri, createNewFile(),)
    }

    override fun createNewPlaylist(playlist: Playlist): Flow<Playlist> {

        return newPlaylistRepository.insertPlaylist(playlist)
    }

    override fun saveBitmapToFile(croppedByteArray: ByteArray): Flow<String> {

        return newPlaylistRepository.saveBitmapToFile(croppedByteArray, createNewFile(),)
    }

    override fun getImagesFromStorage(): Flow<List<String>> {
        return newPlaylistRepository.getImagesFromStorage()
    }

    override fun deleteFile(fileUrl: String): Flow<Boolean> {
        val path: String = fileUrl
        val file = File(path)
        return newPlaylistRepository.deleteFile(file)
    }

    override fun loadPlaylistFlow(playlistId: ExtraActionBundleKey?): Flow<Playlist?> {
        return  if(playlistId ==null || playlistId.id < 0) {
                flow { emit(null) }
            } else {
            newPlaylistRepository.getPlaylistByIdFlow(playlistId.id)
        }
    }

    override fun processPlaylist(playlist: Playlist, editFlag: Boolean): Flow<Playlist> {

        return  if(editFlag) {
            newPlaylistRepository.updatePlaylist(playlist)
        } else {
            createNewPlaylist(playlist)
        }
    }
}
