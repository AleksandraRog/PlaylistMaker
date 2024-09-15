package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.search_button)
        val settingButton = findViewById<Button>(R.id.setting_button)
        val libraryButton = findViewById<Button>(R.id.library_button)

//        val searchButtonClickListener: View.OnClickListener = object : View.OnClickListener {
//            override fun onClick(v: View?) {
//                Toast.makeText(this@MainActivity, "Нажали на картинку1!", Toast.LENGTH_SHORT).show()
//            }
//        }
//        searchButton.setOnClickListener(searchButtonClickListener)
//
//        settingButton.setOnClickListener {
//            Toast.makeText(this@MainActivity, "Нажали на картинку2!", Toast.LENGTH_SHORT).show()
//        }
//
//        libraryButton.setOnClickListener {
//            Toast.makeText(this@MainActivity, "Нажали на картинку3!", Toast.LENGTH_SHORT).show()
//        }
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
        }
  }
}