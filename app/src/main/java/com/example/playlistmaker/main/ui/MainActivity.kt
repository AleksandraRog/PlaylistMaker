package com.example.playlistmaker.main.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.common.App
import com.example.playlistmaker.common.ScreenSize
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.library.ui.LibraryActivity
import com.example.playlistmaker.main.presentation.MainViewModel
import com.example.playlistmaker.main.presentation.Screens
import com.example.playlistmaker.search.ui.SearchActivity
import com.example.playlistmaker.settings.presentation.DarkThemeViewModel
import com.example.playlistmaker.settings.ui.SettingsActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var app: App
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModel()
    private val darkThemeViewModel: DarkThemeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {

        app = application as App
        ScreenSize.initSize(this)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left, systemBars.top, systemBars.right,
                systemBars.bottom
            )
            insets
        }

        viewModel.getScreenStateLiveData().observe(this) { screen ->
            navigate(screen)
        }

        darkThemeViewModel.getDarkThemeLiveData().observe(this) {
            window.navigationBarColor = if (it) ContextCompat.getColor(
                this@MainActivity,
                R.color.black
            ) else ContextCompat.getColor(
                this@MainActivity,
                R.color.YP_blue_light
            )
        }

        binding.searchButton.setOnClickListener {
            viewModel.navigateTo(Screens.SEARCH)
        }
        binding.libraryButton.setOnClickListener {
            viewModel.navigateTo(Screens.LIBRARY)
        }

        binding.settingButton.setOnClickListener {
            viewModel.navigateTo(Screens.SETTING)
        }

    }

    private fun navigate(screens: Screens) {
        val clazz: Class<out Activity> = when (screens) {
            Screens.SETTING -> SettingsActivity::class.java
            Screens.SEARCH -> SearchActivity::class.java
            Screens.LIBRARY -> LibraryActivity::class.java
        }
        startActivity(Intent(this, clazz))
        finish()
    }

}
