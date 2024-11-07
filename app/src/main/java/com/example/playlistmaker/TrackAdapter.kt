package com.example.playlistmaker

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter() : RecyclerView.Adapter<TrackViewHolder>() {

    var tracks: List<Track> = emptyList()
    var setOnItemClickListener: ((Int) -> Unit)? = null

    fun updateTracks(newTracks: List<Track>) {
        val oldTracks = tracks
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return oldTracks.size
            }

            override fun getNewListSize(): Int {
                return newTracks.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldTracks[oldItemPosition].trackId == newTracks[newItemPosition].trackId

            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldTracks[oldItemPosition] == newTracks[newItemPosition]

            }
        })
        this.tracks = newTracks.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks.get(position)
        holder.bind(track)
        holder.itemView.setOnClickListener {
            Track.addHistoryList(track)
            setOnItemClickListener?.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}
