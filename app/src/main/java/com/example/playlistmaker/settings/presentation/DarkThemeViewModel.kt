package com.example.playlistmaker.settings.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.App
import com.example.playlistmaker.settings.domain.interactors.DarkThemeInteractor
import kotlinx.coroutines.launch


class DarkThemeViewModel(
    application: Application,
    private val settingsInteractor: DarkThemeInteractor,
) : AndroidViewModel(application) {

    private var darkThemeLiveData = MutableLiveData<Pair<Boolean,Boolean?>>()

    val app = application as App
    init {
        viewModelScope.launch {
            settingsInteractor.getDarkThemeFlow()
                .collect { dTheme ->
                    val darkTheme = app.getDarkTheme(dTheme)
                    darkThemeLiveData.value = Pair(darkTheme, null)

                }
        }
    }

    fun getDarkThemeLiveData(): LiveData<Pair<Boolean,Boolean?>> = darkThemeLiveData

    fun configDarkTheme(enableTheme: Boolean) {
        viewModelScope.launch {
        settingsInteractor.saveDarkTheme(enableTheme)
        val previous = darkThemeLiveData.value?.first
        darkThemeLiveData.postValue(Pair(enableTheme, previous))
    }}

    fun configPreviousDarkTheme  (enableTheme: Boolean) {
        val previous = darkThemeLiveData.value?.first
        darkThemeLiveData.postValue(Pair(previous!!, enableTheme))
    }
}
