package com.example.playlistmaker.new_playlist.data.repsitory

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.net.toUri
import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.domain.repsitory.DbPlaylistTableRepository
import com.example.playlistmaker.new_playlist.data.file.GetValueFileString
import com.example.playlistmaker.new_playlist.domain.repository.NewPlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal
import java.math.RoundingMode

class NewPlaylistRepositoryImpl(private val repositoryDb: DbPlaylistTableRepository,
                                private val application: Application,
                                private val picturesLocalDir: File?,
                                private val getValueFileString: GetValueFileString,
): NewPlaylistRepository {

    // file
    override fun createNewFile(fileName: String): File {

        val filePath = File(picturesLocalDir, getValueFileString.directoryName)

        if (!filePath.exists()) { filePath.mkdirs() }

        return File(filePath, fileName)
    }

    override fun saveImageToPrivateStorage(uri: String, createdFile : File,): Flow<String> = flow {
        withContext(Dispatchers.IO){

            application.contentResolver.openInputStream(uri.toUri())?.use { inputStream ->
                FileOutputStream(createdFile).use { outputStream ->
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    bitmap?.compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
                }
            }
        }
        emit(createdFile.absolutePath)
    }

    override fun getImageAspectRatio(uri: String): Flow<Float> = flow {
        val ratioOptions = withContext(Dispatchers.IO){
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            application.contentResolver.openInputStream(uri.toUri())?.use {
                BitmapFactory.decodeStream(it, null, options)
            }
            if (options.outWidth > 0 && options.outHeight > 0) {
                BigDecimal(options.outWidth.toDouble() / options.outHeight)
                    .setScale(2, RoundingMode.HALF_UP)
                    .toFloat()
            } else {
                0f
            }
        }
        emit(ratioOptions)
    }

    override fun getImagesFromStorage(): Flow<List<String>> = flow {

        val albumDir = File(picturesLocalDir, getValueFileString.directoryName)
        withContext (Dispatchers.IO){
            val files = albumDir.listFiles()
            files?.forEach { file ->

            }
            val uris = files?.map { file -> file.toUri().toString() } ?: emptyList()
            emit(uris)
        }
    }

    override fun saveBitmapToFile(croppedByteArray: ByteArray, createdFile: File): Flow<String> = flow {
        withContext(Dispatchers.IO) {
            FileOutputStream(createdFile).use { outputStream ->
                outputStream.write(croppedByteArray)
            }
            createdFile
        }
        emit(createdFile.absolutePath)
    }

    override fun deleteFile(file: File): Flow<Boolean> =  flow{
        withContext(Dispatchers.IO){
            file.delete()
        }
        emit(false)
    }

    // db
    override fun insertPlaylist(playlist: Playlist): Flow<Playlist> {
        return repositoryDb.insertPlaylist(playlist)
    }

    override fun updatePlaylist(playlist: Playlist): Flow<Playlist> {
        return repositoryDb.updatePlaylist(playlist)
    }

    override fun getPlaylistByIdFlow(id: Int): Flow<Playlist> {
        return repositoryDb.getPlaylist(id)
    }
}
