package com.example.playlistmaker.common.di

import com.example.playlistmaker.search.domain.usecase.SaveHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.UpdateHistoryQueueUseCase
import org.koin.dsl.module


val useCaseModule = module {

    single<UpdateHistoryQueueUseCase> {
        UpdateHistoryQueueUseCase(get(),)
    }

    single<SaveHistoryUseCase> {
        SaveHistoryUseCase(get())
    }
}
