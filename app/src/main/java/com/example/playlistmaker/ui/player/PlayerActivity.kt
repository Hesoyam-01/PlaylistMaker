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

    private lateinit var playStopButton: ImageView
    private lateinit var elapsedTime: TextView
    private var previewUrl: String? = ""
    private var mediaPlayer = MediaPlayer()
    private var playerState = MEDIA_STATE_DEFAULT

    private val dateFormat by lazy { SimpleDateFormat("m:ss", Locale.getDefault()) }

    private val updateElapsedTimeRunnable = object : Runnable {
        override fun run() {
            if (playerState == MEDIA_STATE_PLAYING) {
                elapsedTime.text = dateFormat
                    .format(mediaPlayer.currentPosition)
                handler.postDelayed(this, ELAPSED_TIME_UPDATE_DELAY)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        previewUrl = intent.getStringExtra("PREVIEW_URL")
        preparePlayer()

        elapsedTime = findViewById(R.id.elapsed_time)
        playStopButton = findViewById(R.id.play_stop_button)
        playStopButton.setOnClickListener {
            playbackControl()
        }

        val yearVisibilityView = findViewById<Group>(R.id.year_visibility)
        val albumVisibilityView = findViewById<Group>(R.id.album_visibility)
        val playerToolbar = findViewById<MaterialToolbar>(R.id.player_toolbar)
        val trackCover = findViewById<ImageView>(R.id.player_track_cover)
        val trackName = findViewById<TextView>(R.id.player_track_name)
        val artistName = findViewById<TextView>(R.id.player_artist_name)
        val trackTime = findViewById<TextView>(R.id.time_info)
        val albumName = findViewById<TextView>(R.id.album_info)
        val genreName = findViewById<TextView>(R.id.genre_info)
        val releaseDate = findViewById<TextView>(R.id.year_info)
        val country = findViewById<TextView>(R.id.country_info)

        playerToolbar.setNavigationOnClickListener {
            finish()
        }

        if (intent.getStringExtra("RELEASE_DATE") == null) yearVisibilityView.visibility = View.GONE
        if (intent.getStringExtra("ALBUM_NAME") == null) albumVisibilityView.visibility = View.GONE

        trackName.text = intent.getStringExtra("TRACK_NAME")
        artistName.text = intent.getStringExtra("ARTIST_NAME")
        trackTime.text = intent.getStringExtra("TRACK_TIME")
        albumName.text = intent.getStringExtra("ALBUM_NAME")
        genreName.text = intent.getStringExtra("GENRE_NAME")
        releaseDate.text = intent.getStringExtra("RELEASE_DATE")
        country.text = intent.getStringExtra("COUNTRY")

        val coverUrl = intent.getStringExtra("TRACK_COVER")
        Glide.with(this)
            .load(coverUrl)
            .transform(RoundedCorners(dpToPx(8)))
            .placeholder(R.drawable.album_placeholder)
            .into(trackCover)

    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playStopButton.isEnabled = true
            playerState = MEDIA_STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = MEDIA_STATE_PREPARED
            playStopButton.setImageResource(R.drawable.ic_play_84)
            elapsedTime.text = dateFormat.format(0)
            handler.removeCallbacks(updateElapsedTimeRunnable)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = MEDIA_STATE_PLAYING
        playStopButton.setImageResource(R.drawable.ic_stop_84)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = MEDIA_STATE_PAUSED
        playStopButton.setImageResource(R.drawable.ic_play_84)
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