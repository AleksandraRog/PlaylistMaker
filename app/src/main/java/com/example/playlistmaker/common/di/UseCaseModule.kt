package com.example.playlistmaker.common.di

import com.example.playlistmaker.domain.usecases.GetTrackByIdUseCase
import com.example.playlistmaker.search.domain.usecase.SaveHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.UpdateHistoryQueueUseCase
import org.koin.dsl.module


val useCaseModule = module {

    single<GetTrackByIdUseCase> {
        GetTrackByIdUseCase(get())
    }

    single<UpdateHistoryQueueUseCase> {
        UpdateHistoryQueueUseCase()
    }

    single<SaveHistoryUseCase> {
        SaveHistoryUseCase(get())
    }
}
