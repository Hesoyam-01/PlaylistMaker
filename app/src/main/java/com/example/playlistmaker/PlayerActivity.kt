package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import com.makeramen.roundedimageview.RoundedImageView

class PlayerActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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
}