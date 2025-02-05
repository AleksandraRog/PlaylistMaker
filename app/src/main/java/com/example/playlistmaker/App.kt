package com.example.playlistmaker

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.consumer.ConsumerData
import com.example.playlistmaker.domain.interactors.DarkThemeInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin




class App() : Application() {

    private val darkThemeInteractor = Creator.provideDarkThemeInteractor()
    private val handler = Handler(Looper.getMainLooper())


    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(experimetnKoinModule)
        }

       darkThemeInteractor.getDarkTheme(consumer = object : DarkThemeInteractor.DarkThemeConsumer{
            override fun consume(data: ConsumerData<Boolean>) {
                handler.post( Runnable {switchTheme(data.result)} )
            }
        })
    }

    fun switchTheme(enableTheme: Boolean) {
        val themeToEnable = enableTheme
        AppCompatDelegate.setDefaultNightMode(
            if (themeToEnable) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
