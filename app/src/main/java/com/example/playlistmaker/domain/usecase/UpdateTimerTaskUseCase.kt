package com.example.playlistmaker.domain.usecase


object UpdateTimerTaskUseCase {

    fun execute (startTime: Long, currentTime: Long) : Long {
        val elapsedTime = System.currentTimeMillis() - startTime
        return currentTime + elapsedTime
    }
}
