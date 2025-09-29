package com.example.playlistmaker.ui.player

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.presentation.player.PlayerViewModel

class PlayerActivity : AppCompatActivity() {
    private val previewUrl: String by lazy {
        intent.getStringExtra("PREVIEW_URL") ?: ""
    }

    private val viewModel: PlayerViewModel by lazy {
        ViewModelProvider(
            this,
            PlayerViewModel.getFactory(previewUrl)
        )[PlayerViewModel::class.java]
    }
    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.observePlayerState().observe(this) {
            changePlayStopButton(it.isPlaying)
            binding.elapsedTime.text = it.elapsedTime
        }

        binding.playerToolbar.setNavigationOnClickListener {
            finish()
        }

        binding.playStopButton.setOnClickListener {
            viewModel.playbackControl()
        }

        if (intent.getStringExtra("RELEASE_DATE") == null) binding.yearView.visibility = View.GONE
        if (intent.getStringExtra("ALBUM_NAME") == null) binding.albumView.visibility = View.GONE

        binding.apply {
            playerTrackName.text = intent.getStringExtra("TRACK_NAME")
            playerArtistName.text = intent.getStringExtra("ARTIST_NAME")
            timeInfo.text = intent.getStringExtra("TRACK_TIME")
            albumInfo.text = intent.getStringExtra("ALBUM_NAME")
            genreInfo.text = intent.getStringExtra("GENRE_NAME")
            yearInfo.text = intent.getStringExtra("RELEASE_DATE")
            countryInfo.text = intent.getStringExtra("COUNTRY")
        }

        val coverUrl = intent.getStringExtra("TRACK_COVER")
        Glide.with(this)
            .load(coverUrl)
            .transform(RoundedCorners(dpToPx(8)))
            .placeholder(R.drawable.album_placeholder)
            .into(binding.playerTrackCover)

    }

    private fun changePlayStopButton(isPlaying: Boolean) {
        binding.apply {
            if (isPlaying) playStopButton.setImageResource(R.drawable.ic_stop_84)
            else playStopButton.setImageResource(R.drawable.ic_play_84)
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }
}