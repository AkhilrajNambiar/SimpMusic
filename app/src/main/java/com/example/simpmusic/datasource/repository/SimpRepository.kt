package com.example.simpmusic.datasource.repository

import com.example.simpmusic.datasource.api.RetrofitInstance.Companion.api

class SimpRepository {
    suspend fun getAllSongs() = api.getAllSongs()
}