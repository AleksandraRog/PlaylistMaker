package com.example.playlistmaker

import android.graphics.Paint
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.track_row_view, parent, false)
) {

    private val imageView: ImageView
    private val trackName: TextView
    private val artistName: TextView
    private val trackTime: TextView

    init {
        imageView = itemView.findViewById(R.id.trackImage)
        trackName = itemView.findViewById(R.id.trackName)
        artistName = itemView.findViewById(R.id.artistName)
        trackTime = itemView.findViewById(R.id.trackTime)
    }

    fun bind(track: Track) {

        Glide.with(itemView.context)
            .load(track.artworkUrl100).centerInside()
            .placeholder(R.drawable.vector_placeholder)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(2f)))
            .into(imageView)
        trackName.setSingleLine(true)
        artistName.setSingleLine(true)
        trackTime.setSingleLine(true)
        trackName.text = track.trackName ?: itemView.context.getString(R.string.no_reply)
        artistName.text = track.artistName ?: itemView.context.getString(R.string.no_reply)
        trackTime.text = track.trackTime.toString() ?: itemView.context.getString(R.string.no_reply)
        trackName.ellipsize = TextUtils.TruncateAt.END
        artistName.ellipsize = TextUtils.TruncateAt.END

        val getTextWidthInPixels: (String, Float) -> Float = { text, textSize ->
            val paint = Paint().apply {
                this.textSize = textSize
                this.typeface = ResourcesCompat.getFont(itemView.context, R.font.ys_display_regular)
            }
            paint.measureText(text)
        }
        val textWidthInPx =
            getTextWidthInPixels(
                track.trackTime.toString() ?: itemView.context.getString(R.string.no_reply),
                11f * itemView.context.resources.displayMetrics.density
            )
        artistName.maxWidth =
            ScreenSize.getScreenWidth() - ScreenSize.getOtherWidthTrackViewHolder() - textWidthInPx.toInt()
    }

    fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp,
            itemView.context.resources.displayMetrics
        ).toInt()
    }
}
