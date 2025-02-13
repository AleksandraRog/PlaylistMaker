package com.example.playlistmaker.search.presentation

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.common.domain.consumer.Consumer
import com.example.playlistmaker.common.domain.consumer.ConsumerData
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.search.domain.interactors.HistoryInteractor
import com.example.playlistmaker.search.domain.interactors.TracksInteractor
import com.example.playlistmaker.search.domain.model.HistoryQueue
import com.example.playlistmaker.search.domain.usecase.UpdateHistoryQueueUseCase
import java.util.LinkedList

class SearchViewModel(
    application: Application,
    private val historyInteractor: HistoryInteractor,
    private val tracksInteractor: TracksInteractor,
    private val updateHistoryQueueUseCase: UpdateHistoryQueueUseCase
) : AndroidViewModel(application) {

    private var lastSearchText: String? = null
    private val handler = Handler(Looper.getMainLooper())

    val tracks = ArrayList<Track>()
    var historyTracks = HistoryQueue(LinkedList<Track>())

    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    private fun loadHistory() {

        historyInteractor.loadTracks(consumer = object : HistoryInteractor.HistoryTracksConsumer {
            override fun consume(data: ConsumerData<LinkedList<Track>>) {
                historyTracks = HistoryQueue(data.result)

                val tracksState =
                    if (historyTracks.size != 0) TracksState.History(historyTracks) else TracksState.EmptyHistory
                stateLiveData.postValue(tracksState)
            }
        })
    }

    private fun searchDebounce(changedText: String) {
        if (lastSearchText == changedText) {
            return
        }
        this.lastSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val searchRunnable = Runnable { search(changedText) }
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    private fun search(newSearchText: String): Boolean {

        if (newSearchText.isNotEmpty()) {
            tracksInteractor.searchTracks(
                newSearchText,
                consumer = object : TracksInteractor.FindTracksConsumer {
                    override fun consume(data: ConsumerData<List<Track>>) {

                        tracks.clear()
                        if (data.code == 200 && data.result.isNotEmpty()) {
                            tracks.addAll(data.result)
                            stateLiveData.postValue(TracksState.Content(tracks))
                        } else {
                            showMessage(data.code)
                        }
                    }
                })
        }
        return true
    }

    fun showMessage(responseCode: Int) {
        stateLiveData.postValue(if (responseCode == 0) TracksState.Empty else TracksState.LinkError)
    }

    fun showTrack(track: Track) {

        updateHistoryQueueUseCase.execute(
            track,
            historyTracks,
            consumer = object : Consumer<Track> {
                override fun consume(data: ConsumerData<Track>) {
                    stateLiveData.postValue(TracksState.AnyTrack(data.result.trackId))
                }
            }
        )
    }

    fun showFoundTracks(changedText: String) {
        stateLiveData.postValue(TracksState.Loading)
        searchDebounce(changedText)
    }

    fun showHistory() {
        stateLiveData.postValue(TracksState.Loading)
        loadHistory()
    }

    fun clearHistory() {
        historyTracks.clear()
        stateLiveData.postValue(TracksState.EmptyHistory)
    }

    companion object {
        private val SEARCH_REQUEST_TOKEN = Any()
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
