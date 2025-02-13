package com.example.playlistmaker.settings.domain.repository

import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.common.domain.consumer.ListenerConsumer

interface DarkThemeRepository {

    fun getDarkTheme() : ConsumerData<Boolean>

    fun saveDarkTheme(darkTheme: Boolean)

    fun observeThemeChanges (lisenerConsumer: ListenerConsumer<Boolean>)
}
