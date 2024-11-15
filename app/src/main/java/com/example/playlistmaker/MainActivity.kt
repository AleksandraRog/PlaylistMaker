package com.example.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat
import org.koin.java.KoinJavaComponent.getKoin

class MainActivity : AppCompatActivity() {

    val preferences: SharedPreferences by lazy {
        getKoin().get<SharedPreferences>()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        setOnApplyWindowInsetsListener(findViewById<View>(R.id.main_activity)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
           view.setPadding(systemBars.left, systemBars.top, systemBars.right,
                systemBars.bottom)
            insets
        }

        ScreenSize.initSize(this)

        val darkTheme = preferences.getBoolean(DARK_THEME_KEY, false)

        window.navigationBarColor = if (darkTheme) ContextCompat.getColor(this, R.color.black) else ContextCompat.getColor(this, R.color.YP_blue_light)

        val searchButton = findViewById<Button>(R.id.search_button)
        val settingButton = findViewById<Button>(R.id.setting_button)
        val libraryButton = findViewById<Button>(R.id.library_button)

        searchButton.setOnClickListener {
            val searchIntent = Intent(this, SearchActivity::class.java)
            startActivity(searchIntent)
        }
        libraryButton.setOnClickListener {
            val libraryIntent = Intent(this, LibraryActivity::class.java)
            startActivity(libraryIntent)
        }
        settingButton.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
            finish()
        }
    }
}
