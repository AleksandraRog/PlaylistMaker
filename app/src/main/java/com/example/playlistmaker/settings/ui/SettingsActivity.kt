package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.common.App
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.main.ui.MainActivity
import com.example.playlistmaker.settings.presentation.DarkThemeViewModel
import com.example.playlistmaker.sharing.domain.model.EmailData
import com.example.playlistmaker.sharing.presentation.SharingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var app: App
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharingsViewModel: SharingsViewModel
    private lateinit var darkThemeViewModel: DarkThemeViewModel

    private var intentValue: Intent? = null
    private var isDarkMode: Boolean? = null
    private var changeModeListener: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        app = application as App
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        sharingsViewModel = ViewModelProvider(
            this,
            SharingsViewModel.getViewModelFactory(
                getString(R.string.link_course),
                getString(R.string.link_arrow),
                EmailData(
                    getString(R.string.support_email),
                    getString(R.string.theme_support_message),
                    getString(R.string.text_support_message)
                )
            )
        )[SharingsViewModel::class.java]

        darkThemeViewModel = ViewModelProvider(
            this,
            DarkThemeViewModel.getViewModelFactory()
        )[DarkThemeViewModel::class.java]

        darkThemeViewModel.getDarkThemeLiveData().observe(this){

            binding.switchTheme.isChecked = it
            isDarkMode = it
            changeModeListener = it
            app.switchTheme(it)
            reColors(window.decorView as ViewGroup)
            window.decorView.systemUiVisibility = systemUiVisibility(it)
            changeModeListener = null
        }

        sharingsViewModel.getSharingObjectLiveData().observe(this) {

            if (it.intent is Intent) {
                intentValue = it.intent as Intent
                startActivity(intentValue)
            }

        }

        setSupportActionBar(binding.topToolbarFrame)

        binding.topToolbarFrame.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            darkThemeViewModel.configDarkTheme(isChecked)
        }

        binding.buttonSupport.setOnClickListener {
            sharingsViewModel.openSupport()
        }

        binding.buttonShare.setOnClickListener {
            sharingsViewModel.shareApp()
        }

        binding.buttonArrow.setOnClickListener {
            sharingsViewModel.openTerms()
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
            if (currentNightMode == true) {
                binding.buttonSupport.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.YP_white
                    )
                )
                binding.buttonArrow.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.YP_white
                    )
                )
                binding.buttonShare.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.YP_white
                    )
                )
            } else {
                binding.buttonSupport.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.YP_text_grey
                    )
                )
                binding.buttonArrow.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.YP_text_grey
                    )
                )
                binding.buttonShare.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.YP_text_grey
                    )
                )
            }

        } else if (currentNightMode == false) {
            when (intentValue?.action) {
                Intent.ACTION_SENDTO -> binding.buttonSupport.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.YP_black
                    )
                )

                Intent.ACTION_SEND -> binding.buttonShare.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.YP_black
                    )
                )

                Intent.ACTION_VIEW -> binding.buttonArrow.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.YP_black
                    )
                )
            }
        }
    }

    private fun reColors(viewGroup: ViewGroup) {
        val iconColor = if (isDarkMode == true)
            ContextCompat.getColor(this, R.color.YP_white) else
            ContextCompat.getColor(this, R.color.YP_text_grey)
        val textColor = if (isDarkMode == true)
            ContextCompat.getColor(this, R.color.YP_white) else
            ContextCompat.getColor(this, R.color.YP_black)
        val backgroundColor = if (isDarkMode == true)
            ContextCompat.getColor(this, R.color.YP_black) else
            ContextCompat.getColor(this, R.color.YP_white)
        val navigationBarColor = if (isDarkMode == true)
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
        if (changeModeListener == null) {
            super.recreate()
        }
    }

    private fun systemUiVisibility(isLightBackground: Boolean): Int {
        return if (isLightBackground) 0 else
            (View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
    }
}
