package com.example.simpmusic.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
        val showMessage = view.findViewById<TextView>(R.id.tv_message)
        val reloadButton = view.findViewById<Button>(R.id.bt_reload)
        val progressBar = view.findViewById<ProgressBar>(R.id.pb_pagination)

        recyclerView = view.findViewById(R.id.rv_playlists)

        playlistAdapter = PlaylistAdapter(this)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = playlistAdapter
            addOnScrollListener(onScollListener)
        }

        viewModel.musicResponse.observe(viewLifecycleOwner, Observer { resource ->
            when(resource) {
                is Resource.Success -> {
                    loadingGif.visibility = View.INVISIBLE
                    hideProgressBar(progressBar)
                    resource.data?.let {
                        viewModel.musicList.addAll(it.shorts)
                        playlistAdapter.differ.submitList(viewModel.musicList.toList())
                    }
                }
                is Resource.Loading -> {
                    if (viewModel.musicList.isEmpty()) {
                        loadingGif.visibility = View.VISIBLE
                        showMessage.text = resources.getText(R.string.preparing_music)
                        reloadButton.visibility = View.GONE
                        Glide.with(this).asGif().load(R.drawable.monkey_cymbals).into(monkeyLoad)
                    }
                    else {
                        showProgressBar(progressBar)
                    }
                }
                is Resource.Error -> {
                    loadingGif.visibility = View.VISIBLE
                    hideProgressBar(progressBar)
                    showMessage.text = resource.message
                    monkeyLoad.setImageResource(R.drawable.meditatingmonkey)
                    reloadButton.setOnClickListener {
                        viewModel.getAllSongs()
                    }
                }
            }
        })
    }

    var isScrolling = false
    var isLoading = false

    val onScollListener = object: RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val totalItems = layoutManager.itemCount
            val visibleItems = layoutManager.childCount
            val scrolledItems = layoutManager.findFirstVisibleItemPosition()
            if (isScrolling && totalItems == visibleItems + scrolledItems && !isLoading) {
                viewModel.getAllSongs()
            }
            isScrolling = false
        }
    }

    fun showProgressBar(bar: ProgressBar) {
        bar.visibility = View.VISIBLE
        isLoading = true
    }

    fun hideProgressBar(bar: ProgressBar) {
        bar.visibility = View.GONE
        isLoading = false
    }

    override fun onClick(position: Int) {
        val items = SongsList(playlistAdapter.differ.currentList.toMutableList())
        val action = PlayListsFragmentDirections.actionPlayListsFragmentToMusicPlayerFragment(items, position)
        findNavController().navigate(action)
    }
}