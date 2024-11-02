package com.example.playlistmaker

import android.app.Application
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var supportButton: ImageView
    private lateinit var shareButton: ImageView
    private lateinit var arrowButton: ImageView
    private lateinit var switchThemes: SwitchMaterial
    private var intentValue: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        supportButton = findViewById<ImageView>(R.id.button_support)
        shareButton = findViewById<ImageView>(R.id.button_share)
        arrowButton = findViewById<ImageView>(R.id.button_arrow)
        switchThemes = findViewById<SwitchMaterial>(R.id.switch_theme)
        val sharedPreferences = getSharedPreferences(App.PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("DARK_THEME_KEY", false)

        switchThemes.isChecked = isDarkMode

        val topToolbar: Toolbar = findViewById(R.id.top_toolbar_frame)
        setSupportActionBar(topToolbar)

        topToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        switchThemes.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit()
                .putBoolean(App.DARK_THEME_KEY, isChecked)
                .apply()
            (applicationContext as App).switchTheme(isChecked)
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
                Intent.ACTION_SENDTO -> supportButton.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.YP_black
                    )
                )

                Intent.ACTION_SEND -> shareButton.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.YP_black
                    )
                )

                Intent.ACTION_VIEW -> arrowButton.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.YP_black
                    )
                )
            }
        }
    }
}
