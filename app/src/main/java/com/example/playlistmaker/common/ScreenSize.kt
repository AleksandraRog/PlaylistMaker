package com.example.playlistmaker.common

import android.content.Context
import android.util.TypedValue

object ScreenSize {

    private var screenWidth: Int = 0
    private var screenHeight: Int = 0
    private var otherWidthTrackViewHolder: Int = 0

    fun initSize(context: Context) {
        if (screenWidth == 0 || screenHeight == 0) {
            val displayMetrics = context.resources.displayMetrics
            screenWidth = displayMetrics.widthPixels
            screenHeight = displayMetrics.heightPixels
        }
    }

    fun initSizeTrackViewHolder(context: Context) {
        if (otherWidthTrackViewHolder == 0) {
            otherWidthTrackViewHolder = dpToPx(13f + 45f + 8f + 13f + 4f + 24f + 12f, context)
        }
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp,
            context.resources.displayMetrics
        ).toInt()
    }

    fun getScreenWidth(): Int = screenWidth
    fun getScreenHeight(): Int = screenHeight
    fun getOtherWidthTrackViewHolder(): Int = otherWidthTrackViewHolder

    fun getGuidePercentForNewPlaylistActivity(
        bottomHeight: Int,
        limitGuidelineHeight: Int,
        baseGuidelineHeight: Int,
        imeBottomInset: Int,
    ): Float {
        val deltaHeight =  imeBottomInset - (screenHeight-bottomHeight)
        return if (deltaHeight > 0) (baseGuidelineHeight - deltaHeight)
            .coerceIn(limitGuidelineHeight, screenHeight)
            .toFloat() / screenHeight
        else 1f

    }
}
