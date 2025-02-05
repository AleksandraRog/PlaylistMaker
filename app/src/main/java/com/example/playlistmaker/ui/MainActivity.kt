package com.example.playlistmaker.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.ScreenSize
import com.example.playlistmaker.domain.consumer.ConsumerData
import com.example.playlistmaker.domain.interactors.DarkThemeInteractor
import com.example.playlistmaker.ui.tracks_list.SearchActivity

class MainActivity : AppCompatActivity() {

    private val darkThemeInteractor = Creator.provideDarkThemeInteractor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        setOnApplyWindowInsetsListener(findViewById<View>(R.id.main_activity)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left, systemBars.top, systemBars.right,
                systemBars.bottom
            )
            insets
        }

        ScreenSize.initSize(this)

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
        val context = this
        darkThemeInteractor.getDarkTheme(consumer = object : DarkThemeInteractor.DarkThemeConsumer {
            override fun consume(data: ConsumerData<Boolean>) {
                Handler.createAsync(Looper.getMainLooper()).post(Runnable {

                    window.navigationBarColor = if (data.result) ContextCompat.getColor(
                        context,
                        R.color.black
                    ) else ContextCompat.getColor(
                        context,
                        R.color.YP_blue_light
                    )
                })

            }

        })
    }
}
