package com.example.playlistmaker.main.ui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.common.App
import com.example.playlistmaker.common.ScreenSize
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.main.presentation.MainViewModel
import com.example.playlistmaker.settings.presentation.DarkThemeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var app: App
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModel()
    private val darkThemeViewModel: DarkThemeViewModel by viewModel()
    private var changeModeListener: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        app = application as App
        ScreenSize.initSize(this)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val paddingBottom =
                if (ime.bottom > 0) {
                    binding.bottomNavigationView.visibility = View.GONE
                    ime.bottom
                } else {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                    0
                }

            view.setPadding(
                systemBars.left, systemBars.top, systemBars.right,
                paddingBottom
            )
            insets
        }

        darkThemeViewModel.getDarkThemeLiveData().observe(this) {

            if (it.second != null) {

                changeModeListener = it.first
                app.switchTheme(it.first)
                if (it.first) {
                    setTheme(R.style.Base_Theme_PlaylistMaker_Dark)
                } else {
                    setTheme(R.style.Base_Theme_PlaylistMaker_Light)
                }

                window.decorView.systemUiVisibility = systemUiVisibility(it.first)
                changeModeListener = null
            }
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setupWithNavController(navController)
    }

    override fun recreate() {
        if (changeModeListener == null) {
            super.recreate()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        viewModel.focusChange(hasFocus)
    }

    private fun systemUiVisibility(isLightBackground: Boolean): Int {
        return if (isLightBackground) 0 else (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or

            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
    }
}
