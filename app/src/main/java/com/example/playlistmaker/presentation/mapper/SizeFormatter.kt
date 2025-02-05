package com.example.playlistmaker.presentation.mapper

import android.content.Context
import android.util.TypedValue

object SizeFormatter {

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp,
            context.resources.displayMetrics
        ).toInt()
    }
}
