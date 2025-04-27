package com.example.playlistmaker.common.ui.recycler_components.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class RecyclerAdapter<T> : RecyclerView.Adapter<ListViewHolder<T>>(){

    var list: List<T> = emptyList()
    var setOnItemClickListener: ((T) -> Unit)? = null
    var setOnItemLongClickListener: ((T) -> Boolean)? = null

    abstract fun createViewHolder(layoutInspector: LayoutInflater, parent: ViewGroup, viewType: Int = 1): ListViewHolder<T>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder<T> {
        val layoutInspector = LayoutInflater.from(parent.context)
        return createViewHolder(layoutInspector, parent, viewType)
    }

    override fun onBindViewHolder(holder: ListViewHolder<T>, position: Int) {
        val item = list[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            setOnItemClickListener?.invoke(item)
        }
        holder.itemView.setOnLongClickListener{
            setOnItemLongClickListener?.invoke(item) ?: false
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    abstract fun updateList(newList: List<T>)
}
