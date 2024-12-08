package com.example.cse476.weatherapphw4.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cse476.weatherapphw4.models.network.NetworkMonitor

class LoadingViewModel(application: Application) : AndroidViewModel(application) {
    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = this._isLoading

    private val _networkMonitor =
        NetworkMonitor(this.getApplication<Application>().applicationContext)
    val networkState: LiveData<Boolean> = this._networkMonitor.networkState

    fun initializeData() {
        this._isLoading.value = false;
    }
}