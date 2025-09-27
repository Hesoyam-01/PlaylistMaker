package com.example.playlistmaker.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.ui.search.SearchActivity
import com.example.playlistmaker.ui.settings.SettingsActivity
import com.example.playlistmaker.ui.library.LibraryActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchButton.setOnClickListener {
            val searchButtonIntent = Intent(this@MainActivity, SearchActivity::class.java)
            startActivity(searchButtonIntent)
        }


        binding.libraryButton.setOnClickListener {
            val libraryButtonIntent = Intent(this@MainActivity, LibraryActivity::class.java)
            startActivity(libraryButtonIntent)
        }


        binding.settingsButton.setOnClickListener {
            val settingsButtonIntent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(settingsButtonIntent)
        }

    }
}