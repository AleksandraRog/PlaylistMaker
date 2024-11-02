package com.example.playlistmaker

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.LinkedList
import java.util.Queue
import java.util.function.Predicate

class HistoryTracksQueue<T>(private val queue: Queue<T> = LinkedList<T>()) : Queue<T> by queue {


    private val gson = Gson()
    private val sharedPreferences: SharedPreferences =
        App.instance.applicationContext.getSharedPreferences(
            App.PLAYLISTMAKER_PREFERENCES,
            MODE_PRIVATE
        )

    private fun registrationDiff() {
        val queueList = LinkedList(queue)
        var json = gson.toJson(queueList)
        sharedPreferences.edit()
            .putString(App.HISTORY_LIST_KEY, json)
            .apply()
    }

    override fun offer(e: T?): Boolean {
        val result = queue.offer(e)
        registrationDiff()
        return result
    }

    override fun add(element: T?): Boolean {
        val result = queue.add(element)
        registrationDiff()
        return result
    }

    override fun remove(): T {
        val result = queue.remove()
        registrationDiff()
        return result
    }

    override fun poll(): T? {
        val result = queue.poll()
        registrationDiff()
        return result
    }

    override fun peek(): T? {
        val result = queue.peek()
        registrationDiff()
        return result
    }

    override fun clear() {
        queue.clear()
        registrationDiff()

    }

    override fun removeIf(filter: Predicate<in T>): Boolean {
        val result = queue.removeIf(filter)
        registrationDiff()
        return result
    }

    companion object {
        var historyList: HistoryTracksQueue<Track> = HistoryTracksQueue(loadHistoryList())
        val MAX_HISTORYLIST_SIZE = 10

        fun addHistoryList(track: Track) {
            historyList.removeIf { it.trackId == track.trackId }
            if (historyList.size == MAX_HISTORYLIST_SIZE) {
                historyList.poll()
            }
            historyList.offer(track)
        }

        private fun loadHistoryList(): LinkedList<Track> {
            val json = App.instance.applicationContext.getSharedPreferences(
                App.PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE
            ).getString(App.HISTORY_LIST_KEY, null)
            return if (json != null) {
                Gson().fromJson(json, object : TypeToken<LinkedList<Track>>() {}.type)
            } else LinkedList<Track>()
        }

    }
}
