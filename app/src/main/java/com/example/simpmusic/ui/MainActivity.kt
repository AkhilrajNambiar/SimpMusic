package com.example.simpmusic.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.simpmusic.R
import com.example.simpmusic.datasource.repository.SimpRepository
import com.example.simpmusic.util.Resource
import com.example.simpmusic.viewmodels.SimpViewModel
import com.example.simpmusic.viewmodels.SimpViewModelProviderFactory

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: SimpViewModel
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Creating the viewModel using the ViewModelProvider and factory
        val repository = SimpRepository()
        val viewModelFactory = SimpViewModelProviderFactory(application, repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[SimpViewModel::class.java]

        // Setting up the NavHostFragment, which will act as a container
        // for the fragments
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController
    }
}