package com.example.playlistmaker.new_playlist.presentation

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.new_playlist.domain.interactors.MakePlaylistInteractor
import com.example.playlistmaker.new_playlist.presentation.BitmapMapper.toByteArrayAsync
import kotlinx.coroutines.launch

class MakePlaylistViewModel(private val interactor: MakePlaylistInteractor,) : ViewModel() {

    private var screenStateLiveData = MutableLiveData<CreatePlaylistScreenState>()
    fun getScreenStateLiveData(): LiveData<CreatePlaylistScreenState> = screenStateLiveData

    private fun saveUriToFile(uri: Uri) {
        viewModelScope.launch {
            interactor.saveUriToFile(uri.toString())
                .collect { url ->
                screenStateLiveData.postValue(CreatePlaylistScreenState.InstallLogo(url))
            }
        }
    }

    fun getImageAspectRatio(uri: Uri){
        viewModelScope.launch {
            interactor.getImageAspectRatio(uri.toString())
                .collect { aspectRatio ->
                    if (aspectRatio in 0.85..1.15) {
                        saveUriToFile(uri)
                    } else {
                        screenStateLiveData.postValue(CreatePlaylistScreenState.ActivateCropper(uri))
                    }
                }

        }
    }

    fun savePlaylist(playlist: Playlist) {

        viewModelScope.launch {

            interactor.createNewPlaylist(playlist)
                .collect { listState ->

                    screenStateLiveData.postValue(CreatePlaylistScreenState.SavePlaylist(listState))
                }
        }
    }

    fun saveBitmapToFile(croppedBitmap: Bitmap) {
        viewModelScope.launch {
            interactor.saveBitmapToFile(croppedBitmap.toByteArrayAsync(
                Bitmap.CompressFormat.JPEG,
                quality = 30
            ))
                .collect { url ->
                    screenStateLiveData.postValue(CreatePlaylistScreenState.InstallLogo(url))
                }
        }
    }

    fun deleteFile(fileUrl: String) {
        viewModelScope.launch {
            interactor.deleteFile(fileUrl)
                .collect { result ->
                    screenStateLiveData.postValue(CreatePlaylistScreenState.ClosePlaylist(result))
                }
        }
    }

    fun getImagesFromStorage(){
        viewModelScope.launch {
            interactor.getImagesFromStorage()
                .collect { result ->
                    //
                }
        }
    }

    fun finishActivity() {
        screenStateLiveData.postValue(CreatePlaylistScreenState.ClosePlaylist(true))
    }
}
