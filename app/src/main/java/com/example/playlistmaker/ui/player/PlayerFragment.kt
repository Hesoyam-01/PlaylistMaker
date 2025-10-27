package com.example.playlistmaker.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.presentation.player.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {
    private val previewUrl: String by lazy {
        intent.getStringExtra("PREVIEW_URL") ?: ""
    }

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(previewUrl)
    }

    private lateinit var binding: FragmentPlayerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observePlayerState().observe(viewLifecycleOwner) {
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