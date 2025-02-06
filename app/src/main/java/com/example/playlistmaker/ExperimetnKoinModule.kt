package com.example.playlistmaker


import android.content.Context
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.example.playlistmaker.data.local.PLAYLISTMAKER_PREFERENCES
import com.example.playlistmaker.data.local.CustomDateTypeAdapter
import com.example.playlistmaker.data.local.CustomTimeTypeAdapter
import com.example.playlistmaker.domain.model.TrackTimePeriod
import org.koin.dsl.module
import com.google.gson.GsonBuilder
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

val experimetnKoinModule = module {

    single {
        val itunsBaseUrl = "https://itunes.apple.com"
        Retrofit.Builder()
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
    }

   single {
       GsonBuilder()
           .registerTypeAdapter(TrackTimePeriod::class.java, CustomTimeTypeAdapter())
           .registerTypeAdapter(Date::class.java, CustomDateTypeAdapter())
           .create()
   }

    single<ExecutorService> { Executors.newCachedThreadPool() }

    single<Context> { androidApplication() }

    single {
        androidContext().getSharedPreferences(
            PLAYLISTMAKER_PREFERENCES,
            MODE_PRIVATE
        )
    }
}