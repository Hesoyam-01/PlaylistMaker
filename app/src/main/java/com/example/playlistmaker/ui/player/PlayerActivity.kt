package com.example.playlistmaker.ui.player

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private companion object {
        private const val MEDIA_STATE_DEFAULT = 0
        private const val MEDIA_STATE_PREPARED = 1
        private const val MEDIA_STATE_PLAYING = 2
        private const val MEDIA_STATE_PAUSED = 3
        private const val ELAPSED_TIME_UPDATE_DELAY = 100L
    }

    private val handler = Handler(Looper.getMainLooper())

    private lateinit var binding: ActivityPlayerBinding

    private var previewUrl: String? = ""
    private var mediaPlayer = MediaPlayer()
    private var playerState = MEDIA_STATE_DEFAULT

    private val dateFormat by lazy { SimpleDateFormat("m:ss", Locale.getDefault()) }

    private val updateElapsedTimeRunnable = object : Runnable {
        override fun run() {
            if (playerState == MEDIA_STATE_PLAYING) {
                binding.elapsedTime.text = dateFormat
                    .format(mediaPlayer.currentPosition)
                handler.postDelayed(this, ELAPSED_TIME_UPDATE_DELAY)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        previewUrl = intent.getStringExtra("PREVIEW_URL")
        preparePlayer()

        binding.playStopButton.setOnClickListener {
            playbackControl()
        }

        val yearView = findViewById<Group>(R.id.year_view)
        val albumView = findViewById<Group>(R.id.album_view)
        val playerToolbar = findViewById<MaterialToolbar>(R.id.player_toolbar)
        val playerTrackCover = findViewById<ImageView>(R.id.player_track_cover)
        val playerTrackName = findViewById<TextView>(R.id.player_track_name)
        val playerArtistName = findViewById<TextView>(R.id.player_artist_name)
        val trackInfo = findViewById<TextView>(R.id.time_info)
        val albumInfo = findViewById<TextView>(R.id.album_info)
        val genreInfo = findViewById<TextView>(R.id.genre_info)
        val yearInfo = findViewById<TextView>(R.id.year_info)
        val countryInfo = findViewById<TextView>(R.id.country_info)

        playerToolbar.setNavigationOnClickListener {
            finish()
        }

        if (intent.getStringExtra("RELEASE_DATE") == null) yearView.visibility = View.GONE
        if (intent.getStringExtra("ALBUM_NAME") == null) albumView.visibility = View.GONE

        playerTrackName.text = intent.getStringExtra("TRACK_NAME")
        playerArtistName.text = intent.getStringExtra("ARTIST_NAME")
        trackInfo.text = intent.getStringExtra("TRACK_TIME")
        albumInfo.text = intent.getStringExtra("ALBUM_NAME")
        genreInfo.text = intent.getStringExtra("GENRE_NAME")
        yearInfo.text = intent.getStringExtra("RELEASE_DATE")
        countryInfo.text = intent.getStringExtra("COUNTRY")

        val coverUrl = intent.getStringExtra("TRACK_COVER")
        Glide.with(this)
            .load(coverUrl)
            .transform(RoundedCorners(dpToPx(8)))
            .placeholder(R.drawable.album_placeholder)
            .into(playerTrackCover)

    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            binding.playStopButton.isEnabled = true
            playerState = MEDIA_STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = MEDIA_STATE_PREPARED
            binding.apply {
                playStopButton.setImageResource(R.drawable.ic_play_84)
                elapsedTime.text = dateFormat.format(0)
            }
            handler.removeCallbacks(updateElapsedTimeRunnable)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = MEDIA_STATE_PLAYING
        binding.playStopButton.setImageResource(R.drawable.ic_stop_84)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = MEDIA_STATE_PAUSED
        binding.playStopButton.setImageResource(R.drawable.ic_play_84)
    }

    private fun playbackControl() {
        when (playerState) {
            MEDIA_STATE_PLAYING -> {
                pausePlayer()
                handler.removeCallbacks(updateElapsedTimeRunnable)
            }

            MEDIA_STATE_PREPARED, MEDIA_STATE_PAUSED -> {
                startPlayer()
                handler.postDelayed(updateElapsedTimeRunnable, ELAPSED_TIME_UPDATE_DELAY)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}