package com.example.playlistmaker.settings.presentation

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.common.App
import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.settings.domain.interactors.DarkThemeInteractor


class DarkThemeViewModel(
    application: Application,
    private val settingsInteractor: DarkThemeInteractor,
) : ViewModel() {

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
