package com.example.playlistmaker.common.ui.recycler_components.playlist

import android.text.TextUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.common.presentation.mapper.SizeFormatter
import com.example.playlistmaker.common.presentation.model.ItemPlaylistWrapper
import com.example.playlistmaker.common.ui.model.TypePlaylistBindingWrapper
import com.example.playlistmaker.common.ui.recycler_components.common.ListViewHolder
import com.example.playlistmaker.common.presentation.mapper.TextFormForIntFormatter

class PlaylistViewHolder(private val playlistBinding: TypePlaylistBindingWrapper) : ListViewHolder<ItemPlaylistWrapper>(
    when (playlistBinding) {
        is TypePlaylistBindingWrapper.TypeRectanglePlaylistViewBinding -> playlistBinding.binding
        is TypePlaylistBindingWrapper.TypeRowPlaylistViewBinding -> playlistBinding.binding
    }){

    override fun bind(obj: ItemPlaylistWrapper) {
        when (playlistBinding) {
            is TypePlaylistBindingWrapper.TypeRectanglePlaylistViewBinding,
            is TypePlaylistBindingWrapper.TypeRowPlaylistViewBinding -> {

                val cornerSize = if (playlistBinding is TypePlaylistBindingWrapper.TypeRowPlaylistViewBinding) 2f else 8f
                Glide.with(itemView.context)
                    .load(obj.playlist.fileUrl)
                    .error(R.drawable.vector_placeholder)
                    .centerInside()
                    .placeholder(R.drawable.vector_placeholder)
                    .centerCrop()
                    .transform(RoundedCorners(SizeFormatter.dpToPx(cornerSize, itemView.context)))
                    .into(playlistBinding.playlistImage)
                playlistBinding.playlistName.setSingleLine(true)
                playlistBinding.playlistSize.setSingleLine(true)
                playlistBinding.playlistName.text = obj.playlist.playlistName
                playlistBinding.playlistSize.text = TextFormForIntFormatter.pluralFormTrackCount(obj.playlist.playlistSize)
                playlistBinding.playlistName.ellipsize = TextUtils.TruncateAt.END
                playlistBinding.playlistSize.ellipsize = TextUtils.TruncateAt.END
            }
        }
    }
}
