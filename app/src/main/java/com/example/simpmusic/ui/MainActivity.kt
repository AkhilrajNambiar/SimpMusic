package com.example.simpmusic.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.simpmusic.R
import com.example.simpmusic.SimpMusicApplication
import com.example.simpmusic.datasource.models.Short
import com.example.simpmusic.datasource.repository.SimpRepository
import com.example.simpmusic.util.Resource
import com.example.simpmusic.util.SongService
import com.example.simpmusic.viewmodels.SimpViewModel
import com.example.simpmusic.viewmodels.SimpViewModelProviderFactory
import com.google.android.exoplayer2.ExoPlayer

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: SimpViewModel
    lateinit var navController: NavController
    lateinit var player: ExoPlayer
    var currentPlayer: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_SimpMusic)
        setContentView(R.layout.activity_main)
        val currentExoPlayer by lazy {
            ExoPlayer.Builder(applicationContext)
                .setSeekForwardIncrementMs(5000L)
                .setSeekBackIncrementMs(5000L)
                .build()
        }
        currentPlayer = currentExoPlayer
        player = currentExoPlayer

        // Creating the viewModel using the ViewModelProvider and factory
        val repository = SimpRepository()
        val viewModelFactory = SimpViewModelProviderFactory(application, repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[SimpViewModel::class.java]

        // Setting up the NavHostFragment, which will act as a container
        // for the fragments
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent(this, SongService::class.java)
        stopService(intent)
        currentPlayer!!.release()
        currentPlayer = null
    }
}