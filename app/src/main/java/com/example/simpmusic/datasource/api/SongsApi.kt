package com.example.simpmusic.datasource.api

import com.example.simpmusic.datasource.models.SongsResponse
import retrofit2.Response
import retrofit2.http.GET

interface SongsApi {
    @GET("b9f74279-038b-4590-9f96-7c720261294c")
    suspend fun getAllSongs(): Response<SongsResponse>
}