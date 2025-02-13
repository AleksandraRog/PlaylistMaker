package com.example.playlistmaker.common.di


import android.content.Context
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

val experimetnKoinModule = module {

    single<ExecutorService> { Executors.newCachedThreadPool() }

    single<Context> { androidApplication() }
}