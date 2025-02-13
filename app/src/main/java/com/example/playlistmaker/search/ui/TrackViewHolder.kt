package com.example.playlistmaker.search.ui

import android.graphics.Paint
import android.text.TextUtils
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.common.ScreenSize
import com.example.playlistmaker.databinding.TrackRowViewBinding
import com.example.playlistmaker.common.domain.model.Track
import com.example.playlistmaker.common.presentation.mapper.SizeFormatter

class TrackViewHolder(private val binding: TrackRowViewBinding) : RecyclerView.ViewHolder(
    binding.root) {

    fun bind(track: Track) {

        Glide.with(itemView.context)
            .load(track.artworkUrl100).centerInside()
            .placeholder(R.drawable.vector_placeholder)
            .centerCrop()
            .transform(RoundedCorners(SizeFormatter.dpToPx(2f, itemView.context)))
            .into(binding.trackImage)
        binding.trackName.setSingleLine(true)
        binding.artistName.setSingleLine(true)
        binding.trackTime.setSingleLine(true)
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTime.text = track.trackTime.toString()
        binding.trackName.ellipsize = TextUtils.TruncateAt.END
        binding.artistName.ellipsize = TextUtils.TruncateAt.END

        val getTextWidthInPixels: (String, Float) -> Float = { text, textSize ->
            val paint = Paint().apply {
                this.textSize = textSize
                this.typeface = ResourcesCompat.getFont(itemView.context, R.font.ys_display_regular)
            }
            paint.measureText(text)
        }
        val textWidthInPx =
            getTextWidthInPixels(
                track.trackTime.toString(),
                11f * itemView.context.resources.displayMetrics.density
            )
        binding.artistName.maxWidth =
            ScreenSize.getScreenWidth() - ScreenSize.getOtherWidthTrackViewHolder() - textWidthInPx.toInt()
    }
}
