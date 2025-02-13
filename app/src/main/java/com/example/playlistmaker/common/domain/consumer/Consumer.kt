package com.example.playlistmaker.common.domain.consumer

interface Consumer<T> {
    fun consume(data: ConsumerData<T>)
}
