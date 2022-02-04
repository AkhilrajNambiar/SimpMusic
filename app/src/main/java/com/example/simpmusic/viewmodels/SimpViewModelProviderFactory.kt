package com.example.simpmusic.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simpmusic.datasource.repository.SimpRepository

class SimpViewModelProviderFactory(
    private val app: Application,
    private val repository: SimpRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SimpViewModel(app, repository) as T
    }
}