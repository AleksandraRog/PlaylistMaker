package com.example.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

const val PLAYLISTMAKER_PREFERENCES = "PLAYLIST_MAKER_PROJECT"
const val DARK_THEME_KEY = "DARK_THEME_KEY"
const val HISTORY_LIST_KEY = "HISTORY_LIST_KEY"

class App() : Application() {

    private var darkTheme = false
    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreate() {
        super.onCreate()

        val preferences = getSharedPreferences(PLAYLISTMAKER_PREFERENCES, Context.MODE_PRIVATE)
        darkTheme = preferences.getBoolean(DARK_THEME_KEY, false)

        switchTheme(darkTheme)

        listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == DARK_THEME_KEY) {
                val darkThemev = sharedPreferences?.getBoolean(DARK_THEME_KEY, false)
                if (darkThemev != null) {
                    switchTheme(darkThemev)

                }
            }
        }
        preferences.registerOnSharedPreferenceChangeListener(listener)

       startKoin {
            androidContext(this@App)
            modules(experimetnKoinModule)
        }
    }

    fun switchTheme(enableTheme: Boolean) {
        val themeToEnable = enableTheme
        AppCompatDelegate.setDefaultNightMode(
            if (themeToEnable) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
