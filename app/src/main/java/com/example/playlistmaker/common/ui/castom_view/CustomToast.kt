package com.example.playlistmaker.common.ui.castom_view

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.playlistmaker.R

class CustomToast(context: Context, parent: ViewGroup?) : Toast(context) {
    private val layout: View =
        LayoutInflater.from(context).inflate(R.layout.custom_toast, parent, false)
    val textView: TextView = layout.findViewById(R.id.toast_text)
    init {
        view = layout
        duration = LENGTH_SHORT
        setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 100)

    }

    fun setMessage(message: String): CustomToast {
        textView.text = message
        return this
    }
}
