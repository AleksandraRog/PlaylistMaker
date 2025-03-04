package com.example.playlistmaker.settings.domain.interactors

import com.example.playlistmaker.common.domain.consumer.Consumer
import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.settings.presentation.ThreadFlag

interface DarkThemeInteractor {

    fun getDarkTheme(threadFlag: ThreadFlag, consumer: DarkThemeConsumer, )

    fun saveDarkTheme(darkTheme: Boolean)

    fun  getThemeSync() : ConsumerData<Boolean>

    interface DarkThemeConsumer : Consumer<Boolean>
}
