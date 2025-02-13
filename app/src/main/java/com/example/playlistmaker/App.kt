package com.example.playlistmaker

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.consumer.ConsumerData
import com.example.playlistmaker.domain.interactors.DarkThemeInteractor
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class App() : Application() {

    private val darkThemeInteractor = Creator.provideDarkThemeInteractor()
    private val handler = Handler(Looper.getMainLooper())
    private val context: Context by lazy { getKoin().get<Context>() }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(experimetnKoinModule)
        }

        darkThemeInteractor.getDarkTheme(consumer = object : DarkThemeInteractor.DarkThemeConsumer {
            override fun consume(data: ConsumerData<Boolean>) {
                handler.post(Runnable {
                    switchTheme(getDarkTheme(data))
                })
            }
        })
    }

    fun getDarkTheme(data: ConsumerData<Boolean>): Boolean {
        var darkTheme = data.result
        if (data.code != 0) {
            val configuration = context.resources.configuration
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
