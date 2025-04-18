package com.example.playlistmaker.common.ui.model

import android.widget.ImageView
import android.widget.TextView
import com.example.playlistmaker.databinding.PlaylistRectangleViewBinding
import com.example.playlistmaker.databinding.PlaylistRowViewBinding

sealed class TypePlaylistBindingWrapper {
    abstract val playlistName: TextView
    abstract val playlistSize: TextView
    abstract val playlistImage: ImageView

    class TypeRowPlaylistViewBinding(
        val binding: PlaylistRowViewBinding,) : TypePlaylistBindingWrapper() {
        override val playlistName get() = binding.playlistName
        override val playlistSize get() = binding.playlistSize
        override val playlistImage get() = binding.playlistImage

    }

    class TypeRectanglePlaylistViewBinding(
        val binding: PlaylistRectangleViewBinding,) : TypePlaylistBindingWrapper() {
        override val playlistName get() = binding.playlistName
        override val playlistSize get() = binding.playlistSize
        override val playlistImage get() = binding.playlistImage
    }
}
