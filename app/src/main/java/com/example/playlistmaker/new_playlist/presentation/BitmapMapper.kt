package com.example.playlistmaker.new_playlist.presentation

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

object BitmapMapper {

    suspend fun Bitmap.toByteArrayAsync(bitmapCompressFormat: Bitmap.CompressFormat, quality: Int): ByteArray =
        withContext(Dispatchers.IO) {
        val stream = ByteArrayOutputStream()
        this@toByteArrayAsync.compress(bitmapCompressFormat, quality, stream)
        stream.toByteArray()
    }
}
