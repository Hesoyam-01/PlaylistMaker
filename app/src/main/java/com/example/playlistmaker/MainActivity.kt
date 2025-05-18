package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.search_button)
        searchButton.setOnClickListener {
            Toast.makeText(
                this@MainActivity,
                "Поиск временно не работает.",
                Toast.LENGTH_SHORT
            ).show()
        }

        val libraryButton = findViewById<Button>(R.id.library_button)
        val libraryButtonClickListener : View.OnClickListener = object : View.OnClickListener {
            override fun onClick(p0: View?) {
                Toast.makeText(this@MainActivity, "Медиатека временно не работает.", Toast.LENGTH_SHORT).show()
            }
        }
        libraryButton.setOnClickListener(libraryButtonClickListener)

        val settingsButton = findViewById<Button>(R.id.settings_button)
        settingsButton.setOnClickListener {
            Toast.makeText(
                this@MainActivity,
                "Настройки временно не работают.",
                Toast.LENGTH_SHORT
            ).show()
        }

    }
}