package com.example.playlistmaker.sharing.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.common.domain.consumer.Consumer
import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.sharing.domain.interactors.SharingInteractor

class SharingsViewModel(
    private val sharingInteractor: SharingInteractor,
) : ViewModel() {

    private var sharingLiveData = MutableLiveData<SharingObjects>()
    fun getSharingObjectLiveData(): LiveData<SharingObjects> = sharingLiveData

    fun shareApp() {
        sharingInteractor.shareApp(consumer = object : Consumer<Any?>{
            override fun consume(data: ConsumerData<Any?>) {
                sharingLiveData.postValue(SharingObjects.SHARE_APP.apply { intent=data.result})
            }
        })
    }

    fun openTerms() {
        sharingInteractor.openTerms(consumer = object : Consumer<Any?>{
            override fun consume(data: ConsumerData<Any?>) {
                sharingLiveData.postValue(SharingObjects.TERMS.apply { intent=data.result})
            }
        })
    }

    fun openSupport() {
        sharingInteractor.openSupport(consumer = object : Consumer<Any?>{
            override fun consume(data: ConsumerData<Any?>) {
                sharingLiveData.postValue(SharingObjects.SUPPORT.apply { intent=data.result})
            }
        })
    }
}
