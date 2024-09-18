package com.example.playlistmaker

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class SettingsActivity : AppCompatActivity() {

    private lateinit var  supportButton: ImageView
    private lateinit var  shareButton: ImageView
    private lateinit var  arrowButton: ImageView

    private var intentValue: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        val backButton = findViewById<Button>(R.id.button_back)
        supportButton = findViewById<ImageView>(R.id.button_support)
        shareButton = findViewById<ImageView>(R.id.button_share)
        arrowButton = findViewById<ImageView>(R.id.button_arrow)

        backButton.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
            finish()
        }

        supportButton.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.theme_support_message))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.text_support_message))
            }
            intentValue = supportIntent
            startActivity(supportIntent)
        }

        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.link_course))
            }
            intentValue = shareIntent
            startActivity(shareIntent)
        }

        arrowButton.setOnClickListener {
            val arrowIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_arrow)))
            intentValue = arrowIntent
            startActivity(arrowIntent)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        if (hasFocus) {
            supportButton.clearColorFilter()
            arrowButton.clearColorFilter()
            shareButton.clearColorFilter()

        } else if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
            when (intentValue?.action) {
                Intent.ACTION_SENDTO -> supportButton.setColorFilter(ContextCompat.getColor(this, R.color.YP_black))
                Intent.ACTION_SEND -> shareButton.setColorFilter(ContextCompat.getColor(this, R.color.YP_black))
                Intent.ACTION_VIEW -> arrowButton.setColorFilter(ContextCompat.getColor(this, R.color.YP_black))
            }
        }
    }
}
