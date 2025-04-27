package com.example.playlistmaker.common.ui.model

import android.content.Context
import com.example.playlistmaker.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AlertDialogParameters(
    val title: String? = null,
    val message: String? = null,
    val negativeButtonTitle: String? = null,
    val neutralButtonTitle: String? = null,
    val positiveButtonTitle: String? = null,
    ) {

    private var negativeButtonAction: () -> Unit = {}
    private var neutralButtonAction: () -> Unit = {}
    private var positiveButtonAction: () -> Unit = {}

    fun negativeButtonAction(action: () -> Unit): AlertDialogParameters {
        negativeButtonAction = action
        return this
    }

    fun neutralButtonAction(action: () -> Unit): AlertDialogParameters {
        neutralButtonAction = action
        return this
    }

    fun positiveButtonAction(action: () -> Unit): AlertDialogParameters {
        positiveButtonAction = action
        return this
    }

    fun activateAlertDialog(context: Context): MaterialAlertDialogBuilder {
        val builder = MaterialAlertDialogBuilder(context, R.style.CustomAlertDialogTheme)

        title?.let { builder.setTitle(it) }
        message?.let { builder.setMessage(it) }
        negativeButtonTitle?.let {
            builder.setNegativeButton(it) { dialog, which ->
                negativeButtonAction()
            }
        }
        neutralButtonTitle?.let {
            builder.setNeutralButton(it) { dialog, which ->
                neutralButtonAction()
            }
        }
        positiveButtonTitle?.let {
            builder.setPositiveButton(it) { dialog, which ->
                positiveButtonAction()
            }
        }
        return builder
    }
}
