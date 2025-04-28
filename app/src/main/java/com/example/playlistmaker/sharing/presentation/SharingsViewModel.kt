package com.example.playlistmaker.sharing.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.presentation.model.SharingObjects
import com.example.playlistmaker.sharing.domain.interactors.CompleteSharingInteractor
import kotlinx.coroutines.launch

class SharingsViewModel(
    private val sharingInteractor: CompleteSharingInteractor,
) : ViewModel() {

    private var sharingLiveData = MutableLiveData<SharingObjects>()
    fun getSharingObjectLiveData(): LiveData<SharingObjects> = sharingLiveData

    fun shareApp() {
        getIntentProperty(SharingObjects.SHARE_APP)
    }

    fun openTerms() {
        getIntentProperty(SharingObjects.TERMS)
    }

    fun openSupport() {
        getIntentProperty(SharingObjects.SUPPORT)
    }

    fun restoreState() {
        sharingLiveData.postValue(SharingObjects.DEFAULT)
    }

    private fun getIntentProperty(sharingObjects: SharingObjects) {
        viewModelScope.launch {
            sharingInteractor.getIntentProperty(sharingObjects)
                .collect{ sharingObj ->
                    sharingLiveData.postValue(sharingObj)
                }
        }
    }
}
