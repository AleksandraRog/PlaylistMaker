package com.example.playlistmaker.ui

import android.content.Context
import android.view.Gravity.CENTER_HORIZONTAL
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.mapper.SizeFormatter
import com.google.android.material.progressindicator.CircularProgressIndicator

class CustomCircularProgressIndicator(private val context: Context) {

    fun getCustomCircularProgressIndicator(): CircularProgressIndicator {
        return CircularProgressIndicator(context).apply {
            layoutParams = LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = CENTER_HORIZONTAL
                topMargin = SizeFormatter.dpToPx(140f, context)
            }

            isIndeterminate = true
            indicatorSize = SizeFormatter.dpToPx(44f, context)
            progress = 75
            setIndicatorColor(
                ContextCompat.getColor(
                    context, R.color.YP_blue
                )
            )
        }
    }
}
