package com.example.simpmusic.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.simpmusic.R
import com.example.simpmusic.datasource.models.SongsList
import com.example.simpmusic.ui.MainActivity
import com.example.simpmusic.ui.adapters.PlaylistAdapter
import com.example.simpmusic.util.Resource
import com.example.simpmusic.viewmodels.SimpViewModel

class PlayListsFragment : Fragment(), PlaylistAdapter.MusicClickListener {

    private lateinit var viewModel: SimpViewModel
    private lateinit var playlistAdapter: PlaylistAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = (activity as MainActivity).viewModel
        viewModel.getAllSongs()
        return inflater.inflate(R.layout.fragment_play_lists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val monkeyLoad = view.findViewById<ImageView>(R.id.iv_loading_monkey)
        val loadingGif = view.findViewById<LinearLayout>(R.id.ll_loading_gif)
        recyclerView = view.findViewById(R.id.rv_playlists)

        playlistAdapter = PlaylistAdapter(this)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = playlistAdapter
        }

        viewModel.musicResponse.observe(viewLifecycleOwner, Observer { resource ->
            when(resource) {
                is Resource.Success -> {
                    loadingGif.visibility = View.INVISIBLE
                    resource.data?.let {
                        playlistAdapter.differ.submitList(it.shorts.toList())
                    }
                }
                is Resource.Loading -> {
                    loadingGif.visibility = View.VISIBLE
                    Glide.with(this).asGif().load(R.drawable.monkey_cymbals).into(monkeyLoad)
                }
                is Resource.Error -> {
                    loadingGif.visibility = View.INVISIBLE
                }
            }
        })
    }

    override fun onClick(position: Int) {
        val items = SongsList(playlistAdapter.differ.currentList.toMutableList())
        val action = PlayListsFragmentDirections.actionPlayListsFragmentToMusicPlayerFragment(items, position)
        findNavController().navigate(action)
    }

}