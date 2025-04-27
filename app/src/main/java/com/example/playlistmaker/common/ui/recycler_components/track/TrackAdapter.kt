package com.example.playlistmaker.common.ui.recycler_components.track

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.ui.recycler_components.common.ListViewHolder
import com.example.playlistmaker.common.ui.recycler_components.common.RecyclerAdapter
import com.example.playlistmaker.databinding.TrackRowViewBinding

class TrackAdapter() : RecyclerAdapter<Track>() {

    override fun createViewHolder(
        layoutInspector: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder<Track> {
        return TrackViewHolder(TrackRowViewBinding.inflate(layoutInspector, parent, false))
    }

    override fun updateList(newList: List<Track>) {
        val oldTracks = list
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return oldTracks.size
            }

            override fun getNewListSize(): Int {
                return newList.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldTracks[oldItemPosition].trackId == newList[newItemPosition].trackId
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldTracks[oldItemPosition] == newList[newItemPosition]
            }
        })
        this.list = newList.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }


}
