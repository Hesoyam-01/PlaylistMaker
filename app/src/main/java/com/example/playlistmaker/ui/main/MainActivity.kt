package com.example.playlistmaker.ui.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.presentation.MainViewModel

class MainActivity : AppCompatActivity() {
    private var viewModel: MainViewModel? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel = ViewModelProvider(
            this,
            MainViewModel.getFactory()
        )[MainViewModel::class.java]

        binding.searchButton.setOnClickListener {
            viewModel?.openSearch()
        }


        binding.libraryButton.setOnClickListener {
            viewModel?.openLibrary()
        }


        binding.settingsButton.setOnClickListener {
            viewModel?.openSettings()
        }

    }
}