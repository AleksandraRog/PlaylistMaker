package com.example.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.java.KoinJavaComponent.getKoin


class SettingsActivity : AppCompatActivity() {

    private lateinit var supportButton: ImageView
    private lateinit var shareButton: ImageView
    private lateinit var arrowButton: ImageView
    private lateinit var switchThemes: SwitchMaterial
    private var intentValue: Intent? = null
    private var isDarkMode: Boolean = false

    val preferences: SharedPreferences by lazy {
        getKoin().get<SharedPreferences>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportButton = findViewById<ImageView>(R.id.button_support)
        shareButton = findViewById<ImageView>(R.id.button_share)
        arrowButton = findViewById<ImageView>(R.id.button_arrow)
        switchThemes = findViewById<SwitchMaterial>(R.id.switch_theme)

        isDarkMode = preferences.getBoolean(DARK_THEME_KEY, false)

        switchThemes.isChecked = isDarkMode
        window.decorView.systemUiVisibility = systemUiVisibility(isDarkMode)

        val topToolbar: Toolbar = findViewById(R.id.top_toolbar_frame)
        setSupportActionBar(topToolbar)

        topToolbar.setNavigationOnClickListener {
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }

        switchThemes.setOnCheckedChangeListener { _, isChecked ->

            isDarkMode = isChecked
            reColors(window.decorView as ViewGroup)
            window.decorView.systemUiVisibility = systemUiVisibility(isChecked)
            preferences.edit()
                .putBoolean(DARK_THEME_KEY, isChecked)
                .apply()
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

        val currentNightMode = isDarkMode

        if (hasFocus) {
            if (currentNightMode) {
                supportButton.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.YP_white
                    )
                )
                arrowButton.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.YP_white
                    )
                )
                shareButton.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.YP_white
                    )
                )
            } else {
                supportButton.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.YP_text_grey
                    )
                )
                arrowButton.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.YP_text_grey
                    )
                )
                shareButton.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.YP_text_grey
                    )
                )
                     }

        } else if (!currentNightMode) {
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

    private fun reColors(viewGroup: ViewGroup) {
        val iconColor = if (isDarkMode)
            ContextCompat.getColor(this, R.color.YP_white) else
            ContextCompat.getColor(this, R.color.YP_text_grey)
        val textColor = if (isDarkMode)
            ContextCompat.getColor(this, R.color.YP_white) else
            ContextCompat.getColor(this, R.color.YP_black)
        val backgroundColor = if (isDarkMode)
            ContextCompat.getColor(this, R.color.YP_black) else
            ContextCompat.getColor(this, R.color.YP_white)
        val navigationBarColor = if (isDarkMode)
            ContextCompat.getColor(this, R.color.black) else
            ContextCompat.getColor(this, R.color.YP_white)

        window.statusBarColor = backgroundColor
        window.navigationBarColor = navigationBarColor

        for (i in 0 until viewGroup.childCount) {
            val view = viewGroup.getChildAt(i)
            when (view) {
                is ViewGroup -> {
                    view.setBackgroundColor(backgroundColor)
                    reColors(view)
                }

                is TextView -> {
                    view.setTextColor(textColor)
                    view.setBackgroundColor(backgroundColor)
                }

                is ImageView -> {
                    if (view is ImageButton) {
                        view.setColorFilter(textColor)
                    } else {
                        view.setColorFilter(iconColor)
                    }
                    view.setBackgroundColor(backgroundColor)
                }
            }
        }
    }

    override fun recreate() {

    }

    private fun systemUiVisibility(isLightBackground: Boolean): Int {
        return if (isLightBackground) 0 else
            (View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
    }
}
