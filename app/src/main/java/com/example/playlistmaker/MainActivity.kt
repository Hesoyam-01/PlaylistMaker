package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<MaterialButton>(R.id.search_button)
        val libraryButton = findViewById<MaterialButton>(R.id.library_button)
        val settingsButton = findViewById<MaterialButton>(R.id.settings_button)

        searchButton.setOnClickListener {
            val searchButtonIntent = Intent(this@MainActivity, SearchActivity::class.java)
            startActivity(searchButtonIntent)
        }


        libraryButton.setOnClickListener {
            val libraryButtonIntent = Intent(this@MainActivity, LibraryActivity::class.java)
            startActivity(libraryButtonIntent)
        }


        settingsButton.setOnClickListener {
            val settingsButtonIntent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(settingsButtonIntent)
        }

    }
}