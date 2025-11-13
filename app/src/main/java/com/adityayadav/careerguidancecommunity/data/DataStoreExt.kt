package com.adityayadav.careerguidancecommunity.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

// name of the DataStore file
val Context.dataStore by preferencesDataStore(name = "user_prefs")
