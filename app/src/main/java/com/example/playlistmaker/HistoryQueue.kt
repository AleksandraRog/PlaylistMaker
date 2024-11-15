package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import org.koin.java.KoinJavaComponent.getKoin
import java.util.LinkedList
import java.util.Queue
import java.util.function.Predicate

class HistoryQueue<T>(private val queue: Queue<T> = LinkedList<T>()) : Queue<T> by queue {


    private val gson = GsonBuilder()
        .registerTypeAdapter(TrackTimePeriod::class.java, CustomTimeTypeAdapter())
        .create()

    private val sharedPreferences: SharedPreferences by lazy {
        getKoin().get<SharedPreferences>()
    }

    private fun registrationDiff() {
        val queueList = LinkedList(queue)
        val json = gson.toJson(queueList)
        sharedPreferences.edit()
            .putString(HISTORY_LIST_KEY, json)
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
}
