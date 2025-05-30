package com.example.playlistmaker.common

import android.app.Application
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.BuildConfig
import com.example.playlistmaker.common.di.dataModule
import com.example.playlistmaker.common.di.experimentKoinModule
import com.example.playlistmaker.common.di.interactorModule
import com.example.playlistmaker.common.di.repositoryModule
import com.example.playlistmaker.common.di.useCaseModule
import com.example.playlistmaker.common.di.viewModelModule
import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.settings.domain.interactors.DarkThemeInteractor
import com.markodevcic.peko.PermissionRequester
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level


class App() : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            if (BuildConfig.DEBUG) {
                androidLogger(Level.DEBUG) // Включаем логирование только в debug
            }
            modules(experimentKoinModule, dataModule, interactorModule, repositoryModule,
                useCaseModule, viewModelModule)
        }

        val darkThemeInteractor = getKoin().get<DarkThemeInteractor>()

        applicationScope.launch {
            val darkTheme = darkThemeInteractor.getThemeSync()
            switchTheme(getDarkTheme(darkTheme))
        }

        PermissionRequester.initialize(applicationContext)
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
