package com.example.simpmusic.util

import android.app.Notification
import android.app.PendingIntent
import android.app.PendingIntent.*
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import com.example.simpmusic.*
import com.example.simpmusic.datasource.models.Short
import com.example.simpmusic.ui.MainActivity
import java.io.FileDescriptor
import java.io.PrintWriter

const val TAG = "SongService"

class SongService : Service() {
    lateinit var mediaSessionCompat: MediaSessionCompat

    override fun onCreate() {
        super.onCreate()
        mediaSessionCompat = MediaSessionCompat(this, TAG)
        Log.d("SongService", "onCreateCalled")
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.d("SongService", "onBindCalled")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val title = intent!!.getStringExtra(SONG_TITLE)
        val creator = intent.getStringExtra(SONG_CREATOR)
        val isPlaying = intent.getBooleanExtra(IS_PLAYING, true)
        Log.d("SongService", "onStartCommandCalled")

        val picture = BitmapFactory.decodeResource(resources, R.drawable.monkeywithheadphones)

        val notification = NotificationCompat.Builder(this, CHANNEL_1_ID)
            .setContentTitle(title)
            .setContentText(creator)
            .setSmallIcon(R.drawable.monkeywithheadphones)
            .setLargeIcon(picture)
            .addAction(R.drawable.ic_previous, "Previous", null)
            .addAction(
                if (isPlaying) { R.drawable.ic_pause }
                else { R.drawable.ic_play },
                "Play or Pause", null)
            .addAction(R.drawable.ic_next, "Next", null)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSessionCompat.sessionToken)
            )
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .build()

        startForeground(1, notification)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("SongService", "onDestroy called")
        stopForeground(true)
    }
}