package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.consumer.ConsumerData
import com.example.playlistmaker.domain.consumer.ListenerConsumer

interface DarkThemeRepository {

    fun getDarkTheme() : ConsumerData<Boolean>

    fun saveDarkTheme(darkTheme: Boolean)

    fun observeThemeChanges (lisenerConsumer: ListenerConsumer<Boolean>)
}
