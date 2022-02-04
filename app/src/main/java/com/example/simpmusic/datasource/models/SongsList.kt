package com.example.simpmusic.datasource.models

import java.io.Serializable


data class SongsList(
    val shorts: MutableList<Short>
): Serializable
