package com.example.playlistmaker.common.ui.recycler_components.common

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class ListViewHolder<T>(open val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
    abstract fun bind(obj : T)
}
