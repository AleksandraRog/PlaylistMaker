package com.example.playlistmaker.settings.presentation

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.common.App
import com.example.playlistmaker.common.Creator
import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.settings.domain.interactors.DarkThemeInteractor


class DarkThemeViewModel(
    private val application: Application,
    private val settingsInteractor: DarkThemeInteractor,
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())
    private var darkThemeLiveData = MutableLiveData<Boolean>()
    val app = application as App

    init {
        settingsInteractor.getDarkTheme(
            ThreadFlag.MAIN_THREAD,
            consumer = object : DarkThemeInteractor.DarkThemeConsumer {
                override fun consume(data: ConsumerData<Boolean>) {
                    val darkTheme = app.getDarkTheme(data)
                    darkThemeLiveData.value = darkTheme
                    app.switchTheme(darkTheme)
                }
            })
    }

    fun getDarkThemeLiveData(): LiveData<Boolean> = darkThemeLiveData

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {

                DarkThemeViewModel(
                    this[APPLICATION_KEY] as Application,
                    Creator.provideDarkThemeInteractor()
                )
            }
        }
    }

    fun configDarkTheme(enableTheme: Boolean) {

        settingsInteractor.observeThemeChanges(consumer = object :
            DarkThemeInteractor.DarkThemeConsumer {
            override fun consume(data: ConsumerData<Boolean>) {
                darkThemeLiveData.postValue(data.result)
            }
        })
        settingsInteractor.saveDarkTheme(enableTheme)
    }
}
