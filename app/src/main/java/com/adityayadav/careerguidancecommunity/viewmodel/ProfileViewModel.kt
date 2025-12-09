package com.adityayadav.careerguidancecommunity.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adityayadav.careerguidancecommunity.data.DataStoreManager
import com.adityayadav.careerguidancecommunity.data.model.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val ctx = application.applicationContext

    // Expose a StateFlow<Profile> backed by DataStoreManager.profileFlow
    val profileState: StateFlow<Profile> =
        DataStoreManager.profileFlow(ctx)
            .stateIn(viewModelScope, SharingStarted.Eagerly, Profile(name = "", age = 0, goal = ""))

    /** Save profile to DataStore */
    fun saveProfile(name: String, age: Int, goal: String) {
        viewModelScope.launch(Dispatchers.IO) {
            DataStoreManager.saveProfile(ctx, name, age, goal)
        }
    }

    /** Clear profile saved in DataStore */
    fun clearProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            DataStoreManager.clearProfile(ctx)
        }
    }
}