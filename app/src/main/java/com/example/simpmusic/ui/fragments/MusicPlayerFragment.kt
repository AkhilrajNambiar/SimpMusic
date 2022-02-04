package com.example.simpmusic.ui.fragments

import android.annotation.SuppressLint
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.simpmusic.R
import com.example.simpmusic.datasource.models.SongsList
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.analytics.PlaybackStatsListener
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
        val songName = view.findViewById<TextView>(R.id.tv_song_name)
        val creatorName = view.findViewById<TextView>(R.id.tv_creator_email)
        val loadingGif = view.findViewById<LinearLayout>(R.id.ll_loading_gif)
        val monkeyLoading = view.findViewById<ImageView>(R.id.iv_loading_monkey)
        val songImage = view.findViewById<ImageView>(R.id.iv_song_image)

        val mediaItems = songsList.shorts.map {
            MediaItem.fromUri(it.audioPath)
        }

        songName.text = songsList.shorts[position].title
        creatorName.text = songsList.shorts[position].creator.email

        val player = ExoPlayer.Builder(requireContext())
            .setSeekForwardIncrementMs(5000L)
            .setSeekBackIncrementMs(5000L)
            .build()
            .also {
                playerView.player = it
                it.setMediaItems(mediaItems, position, 0L)
                it.playWhenReady = true
                it.seekTo(0L)
                it.prepare()
                it.addListener(object: Player.Listener{
                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        val index = mediaItems.indexOf(mediaItem!!)
                        songName.text = songsList.shorts[index].title
                        creatorName.text = songsList.shorts[index].creator.email
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when(playbackState) {
                            ExoPlayer.STATE_BUFFERING -> {
                                loadingGif.visibility = View.VISIBLE
                                songImage.visibility = View.INVISIBLE
                                Glide.with(requireContext()).asGif().load(R.drawable.monkey_cymbals).into(monkeyLoading)
                            }
                            ExoPlayer.STATE_READY -> {
                                loadingGif.visibility = View.INVISIBLE
                                songImage.visibility = View.VISIBLE
                            }
                            ExoPlayer.STATE_ENDED -> {
                                loadingGif.visibility = View.INVISIBLE
                                songImage.visibility = View.VISIBLE
                            }
                            ExoPlayer.STATE_IDLE -> {
                                loadingGif.visibility = View.INVISIBLE
                                songImage.visibility = View.VISIBLE
                            }
                        }
                    }
                })
            }
    }

}