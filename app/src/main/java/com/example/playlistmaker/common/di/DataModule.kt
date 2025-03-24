package com.example.playlistmaker.common.di

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.room.Room
import com.example.playlistmaker.common.data.NetworkClient
import com.example.playlistmaker.common.data.SharedPreferencesClient
import com.example.playlistmaker.common.data.db.AppDatabase
import com.example.playlistmaker.common.data.local.CustomDateTypeAdapter
import com.example.playlistmaker.common.data.local.CustomTimeTypeAdapter
import com.example.playlistmaker.common.data.local.PLAYLISTMAKER_PREFERENCES
import com.example.playlistmaker.common.domain.model.TrackTimePeriod
import com.example.playlistmaker.search.data.local.HistoryTrackManager
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.RetrofitTrackApi
import com.example.playlistmaker.settings.data.local.DarkThemeClient
import com.example.playlistmaker.settings.data.local.DarkThemeManager
import com.google.gson.GsonBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date

val dataModule = module {

    factory { MediaPlayer() }

    factory {
        GsonBuilder()
            .registerTypeAdapter(TrackTimePeriod::class.java, CustomTimeTypeAdapter())
            .registerTypeAdapter(Date::class.java, CustomDateTypeAdapter())
            .create()
    }

    single {
        androidContext().getSharedPreferences(
            PLAYLISTMAKER_PREFERENCES,
            MODE_PRIVATE
        )
    }

    single<RetrofitTrackApi> {
        val itunsBaseUrl = "https://itunes.apple.com"
        val retrofit = Retrofit.Builder()
            .baseUrl(itunsBaseUrl)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .registerTypeAdapter(TrackTimePeriod::class.java, CustomTimeTypeAdapter())
                        .registerTypeAdapter(Date::class.java, CustomDateTypeAdapter())
                        .create()
                )
            )
            .build()
        retrofit.create(RetrofitTrackApi::class.java)
    }

    single<HistoryTrackManager> {
        HistoryTrackManager(get(), get())
    }

    single<DarkThemeManager> {
        DarkThemeManager(get())
    }

    single<SharedPreferencesClient> {
        DarkThemeClient(get())
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), get())
    }

    single {
        val androidContext = androidContext()
        val appDatabase = AppDatabase::class.java
        val db = Room.databaseBuilder(androidContext,
            appDatabase, "database.db")
            .build()
        db
    }
}
