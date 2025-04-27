package com.example.playlistmaker.new_playlist.presentation

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.domain.model.Playlist
import com.example.playlistmaker.common.presentation.mapper.BundleMapper.toModel
import com.example.playlistmaker.new_playlist.domain.interactors.MakePlaylistInteractor
import com.example.playlistmaker.new_playlist.presentation.BitmapMapper.toByteArrayAsync
import kotlinx.coroutines.launch

class MakePlaylistViewModel(
    playlistIdBundle: Bundle,
    private val interactor: MakePlaylistInteractor,) : ViewModel() {

        private var screenStateLiveData = MutableLiveData<CreatePlaylistScreenState>()

    init {
        viewModelScope.launch {
            interactor.loadPlaylistFlow(playlistIdBundle.toModel())
                .collect { pair ->
                    if (pair != null)
                        screenStateLiveData.postValue(CreatePlaylistScreenState.LoadPlaylist(pair))
                    else
                        screenStateLiveData.postValue(CreatePlaylistScreenState.Empty)
                }
        }
    }

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

    fun savePlaylist(playlist: Playlist, editFlag: Boolean) {

        viewModelScope.launch {

            interactor.processPlaylist(playlist, editFlag)
                .collect { listState ->
                    screenStateLiveData.postValue(CreatePlaylistScreenState.OnSavePlaylist(listState))
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

