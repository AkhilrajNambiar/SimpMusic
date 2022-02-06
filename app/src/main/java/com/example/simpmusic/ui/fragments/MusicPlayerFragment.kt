package com.example.simpmusic.ui.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.simpmusic.*
import com.example.simpmusic.datasource.models.Short
import com.example.simpmusic.datasource.models.SongsList
import com.example.simpmusic.datasource.models.SongsResponse
import com.example.simpmusic.ui.MainActivity
import com.example.simpmusic.util.SongService
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerControlView

class MusicPlayerFragment : Fragment() {

    private var position = 0
    private lateinit var songsList: SongsList
    private val args by navArgs<MusicPlayerFragmentArgs>()
//    private var currentPlayer: ExoPlayer? = null
    private lateinit var player: ExoPlayer
    private lateinit var mediaSessionCompat: MediaSessionCompat
    private lateinit var notificationManager: NotificationManagerCompat

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        songsList = args.songsList
        position = args.position
        mediaSessionCompat = MediaSessionCompat(requireContext(), "tag")
        notificationManager = NotificationManagerCompat.from(requireContext())
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

//        val player = ExoPlayer.Builder(requireActivity().applicationContext)
//                .setSeekForwardIncrementMs(5000L)
//                .setSeekBackIncrementMs(5000L)
//                .build()
//                .also {
//                    playerView.player = it
//                    it.setMediaItems(mediaItems, position, 0L)
//                    it.playWhenReady = true
//                    it.seekTo(0L)
//                    it.addListener(object: Player.Listener{
//                        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
//                            val index = mediaItems.indexOf(mediaItem!!)
//                            songName.text = songsList.shorts[index].title
//                            creatorName.text = songsList.shorts[index].creator.email
//                        }
//
//                        override fun onPlaybackStateChanged(playbackState: Int) {
//                            when(playbackState) {
//                                ExoPlayer.STATE_BUFFERING -> {
//                                    loadingGif.visibility = View.VISIBLE
//                                    songImage.visibility = View.INVISIBLE
//                                    Glide.with(requireContext()).asGif().load(R.drawable.monkey_cymbals).into(monkeyLoading)
//                                }
//                                ExoPlayer.STATE_READY -> {
//                                    loadingGif.visibility = View.INVISIBLE
//                                    songImage.visibility = View.VISIBLE
//                                }
//                                ExoPlayer.STATE_ENDED -> {
//                                    loadingGif.visibility = View.INVISIBLE
//                                    songImage.visibility = View.VISIBLE
//                                }
//                                ExoPlayer.STATE_IDLE -> {
//                                    loadingGif.visibility = View.INVISIBLE
//                                    songImage.visibility = View.VISIBLE
//                                }
//                            }
//                        }
//                    })
//                }
//        currentPlayer = player
//        currentPlayer!!.prepare()

        // If the player has not been created, show the loading section
        if (!::player.isInitialized) {
            loadingGif.visibility = View.VISIBLE
            songImage.visibility = View.INVISIBLE
            if (isAdded) {
                Glide.with(requireContext()).asGif().load(R.drawable.monkey_cymbals).into(monkeyLoading)
            }
        }

        player = (activity as MainActivity).player
            .also {
                playerView.player = it
                it.setMediaItems(mediaItems, position, 0L)
                it.playWhenReady = true
                it.seekTo(0L)
                it.addListener(object: Player.Listener{
                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        val index = mediaItems.indexOf(mediaItem!!)
                        songName.text = songsList.shorts[index].title
                        creatorName.text = songsList.shorts[index].creator.email
                        startSongService(songsList, index, true)
//                        createNotification(index)
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        val index = mediaItems.indexOf(player.currentMediaItem)
                        startSongService(songsList, index, isPlaying)
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when(playbackState) {
                            ExoPlayer.STATE_BUFFERING -> {
                                loadingGif.visibility = View.VISIBLE
                                songImage.visibility = View.INVISIBLE
                                if (isAdded) {
                                    Glide.with(requireContext()).asGif().load(R.drawable.monkey_cymbals).into(monkeyLoading)
                                }
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
        player.prepare()
        startSongService(songsList, position, true)
//        createNotification(position)
    }

    private fun startSongService(songsList: SongsList, position: Int, isPlaying: Boolean) {
        if (isAdded) {
            val mediaItem = songsList.shorts[position]
            val intent = Intent(requireContext(), SongService::class.java)
            intent.putExtra(SONG_TITLE, mediaItem.title)
            intent.putExtra(SONG_CREATOR, mediaItem.creator.email)
            intent.putExtra(IS_PLAYING, isPlaying)
            requireContext().startService(intent)
        }
    }

}