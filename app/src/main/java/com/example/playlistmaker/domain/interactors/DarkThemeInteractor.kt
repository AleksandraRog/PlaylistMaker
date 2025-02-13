package com.example.playlistmaker.domain.interactors

import com.example.playlistmaker.domain.consumer.Consumer

interface DarkThemeInteractor {

    fun getDarkTheme(consumer: DarkThemeConsumer)

    fun saveDarkTheme(darkTheme: Boolean)

    fun observeThemeChanges(consumer: DarkThemeConsumer)

    interface DarkThemeConsumer : Consumer<Boolean>
}
