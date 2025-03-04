package com.example.playlistmaker.settings.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.common.App
import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.settings.domain.interactors.DarkThemeInteractor


class DarkThemeViewModel(
    application: Application,
    private val settingsInteractor: DarkThemeInteractor,
) : AndroidViewModel(application) {

    private var darkThemeLiveData = MutableLiveData<Pair<Boolean,Boolean?>>()

    val app = application as App
    init {
        settingsInteractor.getDarkTheme(
            ThreadFlag.MAIN_THREAD,
            consumer = object : DarkThemeInteractor.DarkThemeConsumer {
                override fun consume(data: ConsumerData<Boolean>) {
                    val darkTheme = app.getDarkTheme(data)
                    darkThemeLiveData.value= Pair(darkTheme, null)
                }
            })
    }

    fun getDarkThemeLiveData(): LiveData<Pair<Boolean,Boolean?>> = darkThemeLiveData

    fun configDarkTheme(enableTheme: Boolean) {

        settingsInteractor.saveDarkTheme(enableTheme)
        val previous = darkThemeLiveData.value?.first
        darkThemeLiveData.postValue(Pair(enableTheme, previous))
    }

    fun configPreviousDarkTheme  (enableTheme: Boolean) {
        val previous = darkThemeLiveData.value?.first
        darkThemeLiveData.postValue(Pair(previous!!, enableTheme))
    }
}
