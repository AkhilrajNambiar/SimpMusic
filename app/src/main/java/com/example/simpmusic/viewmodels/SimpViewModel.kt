package com.example.simpmusic.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.*
import com.example.simpmusic.SimpMusicApplication
import com.example.simpmusic.datasource.models.Short
import com.example.simpmusic.datasource.models.SongsResponse
import com.example.simpmusic.datasource.repository.SimpRepository
import com.example.simpmusic.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SimpViewModel(
    app: Application,
    private val repository: SimpRepository
): AndroidViewModel(app) {

    private val _musicResponse = MutableLiveData<Resource<SongsResponse>>()
    val musicResponse: LiveData<Resource<SongsResponse>> = _musicResponse

    var musicList = mutableListOf<Short>()

    fun getAllSongs() = viewModelScope.launch(Dispatchers.IO) {
        _musicResponse.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                repository.getAllSongs().let {
                    if (it.isSuccessful) {
                        _musicResponse.postValue(Resource.Success(it.body()!!))
                    }
                    else {
                        _musicResponse.postValue(Resource.Error(null, it.message()))
                    }
                }
            }
            else {
                _musicResponse.postValue(Resource.Error(null, "No internet connection!"))
            }
        }
        catch (e: Throwable) {
            Log.e("getAllSongs", e.stackTraceToString())
            _musicResponse.postValue(Resource.Error(null, "An unknown error occured!"))
        }
    }


    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<SimpMusicApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        // For Android SDK version greater than 23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Get the current network to which the device is connected to, else return false
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            // Get all possible network capabilities of this network
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
        else {
            // For API versions less than 23 this code runs
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }

}