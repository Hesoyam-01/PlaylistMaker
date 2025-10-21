package com.example.playlistmaker.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.domain.models.search.Track
import com.example.playlistmaker.presentation.library.FavoritesFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {
    companion object {
        private const val FAVORITES_LIST = "favorites_list"

        fun newInstance(favoritesList: Boolean) =
            FavoritesFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(FAVORITES_LIST, favoritesList)
                }
            }
    }

    private val viewModel: FavoritesFragmentViewModel by viewModel()

    private lateinit var binding: FragmentFavoritesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}