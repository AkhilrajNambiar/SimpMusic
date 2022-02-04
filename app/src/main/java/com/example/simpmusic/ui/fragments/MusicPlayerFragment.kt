package com.example.simpmusic.ui.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.simpmusic.R
import com.example.simpmusic.datasource.models.SongsList
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView

class MusicPlayerFragment : Fragment() {

    private var position = 0
    private lateinit var songsList: SongsList
    private val args by navArgs<MusicPlayerFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        songsList = args.songsList
        position = args.position
        return inflater.inflate(R.layout.fragment_music_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val playerView = view.findViewById<PlayerControlView>(R.id.pv_music_player)

        val mediaItems = songsList.shorts.map {
            MediaItem.fromUri(it.audioPath)
        }

        val player = ExoPlayer.Builder(requireContext())
            .build()
            .also {
                playerView.player = it
                it.setMediaItems(mediaItems, position, 0L)
                it.playWhenReady = true
                it.seekTo(0L)
                it.prepare()
            }
    }

}