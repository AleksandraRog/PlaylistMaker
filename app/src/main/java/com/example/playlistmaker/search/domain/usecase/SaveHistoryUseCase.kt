package com.example.playlistmaker.search.domain.usecase

import android.util.Log
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.search.domain.reposirory.HistoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import java.util.LinkedList
import java.util.Queue

class SaveHistoryUseCase(private val repository: HistoryRepository) {


    fun execute(queue: Queue<Track>) {
        val job = Job()
        val customScope = CoroutineScope(Dispatchers.IO + job)
        customScope.launch {
            repository.registrationDiff(LinkedList(queue))
                .launchIn(this)
            job.complete()
        }

    }
}
