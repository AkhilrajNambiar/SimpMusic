package com.example.simpmusic.datasource.models

import java.io.Serializable


data class Short(
    val audioPath: String,
    val creator: Creator,
    val dateCreated: String,
    val shortID: String,
    val title: String
): Serializable