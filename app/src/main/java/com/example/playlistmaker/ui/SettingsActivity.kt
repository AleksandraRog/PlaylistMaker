package com.example.playlistmaker.ui

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.playlistmaker.App
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.consumer.ConsumerData
import com.example.playlistmaker.domain.interactors.DarkThemeInteractor
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var supportButton: ImageView
    private lateinit var shareButton: ImageView
    private lateinit var arrowButton: ImageView
    private lateinit var switchThemes: SwitchMaterial
    private lateinit var app: App
    private var intentValue: Intent? = null
    private var isDarkMode: Boolean? = null
    private var changeModeListener: Boolean? = null
    private val handler = Handler(Looper.getMainLooper())
    private val darkThemeInteractor = Creator.provideDarkThemeInteractor()

    override fun onCreate(savedInstanceState: Bundle?) {
        app = application as App
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportButton = findViewById(R.id.button_support)
        shareButton = findViewById(R.id.button_share)
        arrowButton = findViewById(R.id.button_arrow)
        switchThemes = findViewById(R.id.switch_theme)

        getDarkTheme()

        val topToolbar: Toolbar = findViewById(R.id.top_toolbar_frame)
        setSupportActionBar(topToolbar)

        topToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        switchThemes.setOnCheckedChangeListener { _, isChecked ->

            isDarkMode = isChecked
            changeModeListener = isChecked
            reColors(window.decorView as ViewGroup)
            window.decorView.systemUiVisibility = systemUiVisibility(isChecked)
            saveDarkTheme(isChecked)
            changeModeListener = null
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

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val mainIntent = Intent(this@SettingsActivity, MainActivity::class.java)
                startActivity(mainIntent)
                finish()
            }
        })
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        val currentNightMode = isDarkMode

        if (hasFocus) {
            if (currentNightMode==true) {
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

        } else if (currentNightMode==false) {
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
        val iconColor = if (isDarkMode==true)
            ContextCompat.getColor(this, R.color.YP_white) else
            ContextCompat.getColor(this, R.color.YP_text_grey)
        val textColor = if (isDarkMode==true)
            ContextCompat.getColor(this, R.color.YP_white) else
            ContextCompat.getColor(this, R.color.YP_black)
        val backgroundColor = if (isDarkMode==true)
            ContextCompat.getColor(this, R.color.YP_black) else
            ContextCompat.getColor(this, R.color.YP_white)
        val navigationBarColor = if (isDarkMode==true)
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
       if (changeModeListener==null) {
            super.recreate()
        }
    }

    private fun systemUiVisibility(isLightBackground: Boolean): Int {
        return if (isLightBackground) 0 else
            (View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
    }

    private fun saveDarkTheme(darkTheme : Boolean) {
        darkThemeInteractor.saveDarkTheme(darkTheme)
        darkThemeInteractor.observeThemeChanges(consumer = object :DarkThemeInteractor.DarkThemeConsumer{
            override fun consume(data: ConsumerData<Boolean>) {
                handler.post( Runnable {app.switchTheme(data.result)})
            }
        })
    }

    private fun getDarkTheme() {
        darkThemeInteractor.getDarkTheme(consumer = object : DarkThemeInteractor.DarkThemeConsumer{
            override fun consume(data: ConsumerData<Boolean>) {
                handler.post( Runnable {
                    val newDarkTheme = app.getDarkTheme(data)
                    isDarkMode = newDarkTheme
                    switchThemes.isChecked = newDarkTheme
                    window.decorView.systemUiVisibility = systemUiVisibility(newDarkTheme)
                })
            }
        })
    }
}
