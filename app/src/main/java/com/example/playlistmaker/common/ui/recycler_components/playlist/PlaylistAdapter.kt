package com.example.playlistmaker.common.ui.recycler_components.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.playlistmaker.common.presentation.model.ItemPlaylistWrapper
import com.example.playlistmaker.common.ui.model.TypePlaylistBindingWrapper
import com.example.playlistmaker.common.ui.recycler_components.common.ListViewHolder
import com.example.playlistmaker.common.ui.recycler_components.common.RecyclerAdapter
import com.example.playlistmaker.databinding.PlaylistRectangleViewBinding
import com.example.playlistmaker.databinding.PlaylistRowViewBinding

class PlaylistAdapter(private val viewType: Int) : RecyclerAdapter<ItemPlaylistWrapper>() {

    override fun createViewHolder(
        layoutInspector: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder<ItemPlaylistWrapper> {

        return when (viewType) {
            ITEM_VIEW_RECTANGLE -> PlaylistViewHolder(
                TypePlaylistBindingWrapper.TypeRectanglePlaylistViewBinding(
                PlaylistRectangleViewBinding.inflate(layoutInspector, parent, false))
            )
            ITEM_VIEW_ROW -> PlaylistViewHolder(TypePlaylistBindingWrapper.TypeRowPlaylistViewBinding(
                PlaylistRowViewBinding.inflate(layoutInspector, parent, false))
            )
            else -> throw IllegalStateException("There is no ViewHolder for type $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int = viewType

    override fun updateList(newList: List<ItemPlaylistWrapper>) {
        val oldPlaylists = list
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return oldPlaylists.size
            }

            override fun getNewListSize(): Int {
                return newList.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldPlaylists[oldItemPosition].playlist.playlistId == newList[newItemPosition].playlist.playlistId
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldPlaylists[oldItemPosition] == newList[newItemPosition]
            }
        })
        this.list = newList.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }

        companion object {
            const val ITEM_VIEW_RECTANGLE = 1
            const val ITEM_VIEW_ROW = 2
        }
}
