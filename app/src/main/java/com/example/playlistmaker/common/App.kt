package com.example.playlistmaker.common

import android.app.Application
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.common.di.dataModule
import com.example.playlistmaker.common.di.experimetnKoinModule
import com.example.playlistmaker.common.di.interactorModule
import com.example.playlistmaker.common.di.repositoryModule
import com.example.playlistmaker.common.di.useCaseModule
import com.example.playlistmaker.common.di.viewModelModule
import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.settings.domain.interactors.DarkThemeInteractor
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class App() : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(experimetnKoinModule, dataModule, interactorModule, repositoryModule,
                useCaseModule, viewModelModule)
        }
        val darkThemeInteractor = getKoin().get<DarkThemeInteractor>()
        switchTheme(getDarkTheme(darkThemeInteractor.getThemeSync()))
    }

    fun getDarkTheme(data: ConsumerData<Boolean>): Boolean {
        var darkTheme = data.result
        if (data.code != 0) {
            val configuration =  Resources.getSystem().configuration
            darkTheme =
                (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        }

        return darkTheme
    }

    fun switchTheme(enableTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (enableTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
