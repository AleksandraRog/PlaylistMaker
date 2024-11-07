package com.example.playlistmaker

import org.koin.dsl.module
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val experimetnKoinModule = module {

    single {
        val itunsBaseUrl = "https://itunes.apple.com"
        Retrofit.Builder()
            .baseUrl(itunsBaseUrl)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .registerTypeAdapter(TrackTimePeriod::class.java, CustomTimeTypeAdapter())
                        .create()
                )
            )
            .build()
    }

    single {
        get<Retrofit>().create(TrackApi::class.java)
    }
}