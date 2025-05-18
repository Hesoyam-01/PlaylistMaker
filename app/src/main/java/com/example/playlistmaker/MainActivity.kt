package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.search_button)
        val libraryButton = findViewById<Button>(R.id.library_button)
        val settingsButton = findViewById<Button>(R.id.settings_button)

        searchButton.setOnClickListener {
            val searchButtonIntent = Intent(this@MainActivity, SearchActivity::class.java)
            startActivity(searchButtonIntent)
        }


        val libraryButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val libraryButtonIntent = Intent(this@MainActivity, LibraryActivity::class.java)
                startActivity(libraryButtonIntent)
            }
        }
        libraryButton.setOnClickListener(libraryButtonClickListener)


        settingsButton.setOnClickListener {
            val settingsButtonIntent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(settingsButtonIntent)
        }

    }
}