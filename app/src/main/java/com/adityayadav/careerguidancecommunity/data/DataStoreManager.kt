package com.adityayadav.careerguidancecommunity.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.adityayadav.careerguidancecommunity.data.model.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "user_prefs"

// Use a unique extension property name to avoid conflicts with other files.
// If you already have another "dataStore" declaration elsewhere it will no longer clash.
private val Context.appPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

object DataStoreManager {

    // Keys for Profile
    private val KEY_PROFILE_NAME = stringPreferencesKey("profile_name")
    private val KEY_PROFILE_AGE = intPreferencesKey("profile_age")
    private val KEY_PROFILE_GOAL = stringPreferencesKey("profile_goal")

    // Generic key factory for string values (filters/search)
    private fun keyForName(name: String) = stringPreferencesKey(name)

    // ---------------- Profile helpers ----------------

    /**
     * Save a simple Profile (name, age, goal) into preferences.
     */
    suspend fun saveProfile(context: Context, name: String, age: Int, goal: String) {
        context.appPreferencesDataStore.edit { prefs ->
            prefs[KEY_PROFILE_NAME] = name
            prefs[KEY_PROFILE_AGE] = age
            prefs[KEY_PROFILE_GOAL] = goal
        }
    }

    /**
     * Returns a Flow<Profile> that emits the stored profile values (or defaults).
     */
    fun profileFlow(context: Context): Flow<Profile> {
        return context.appPreferencesDataStore.data.map { prefs ->
            val name = prefs[KEY_PROFILE_NAME] ?: ""
            val age = prefs[KEY_PROFILE_AGE] ?: 0
            val goal = prefs[KEY_PROFILE_GOAL] ?: ""
            Profile(name = name, age = age, goal = goal)
        }
    }

    suspend fun clearProfile(context: Context) {
        context.appPreferencesDataStore.edit { prefs ->
            prefs.remove(KEY_PROFILE_NAME)
            prefs.remove(KEY_PROFILE_AGE)
            prefs.remove(KEY_PROFILE_GOAL)
        }
    }

    // ---------------- Generic string helpers (filters, searches) ----------------

    /**
     * Save a string under the provided key name (e.g. "colleges_filter").
     */
    suspend fun saveString(context: Context, keyName: String, value: String) {
        val key = keyForName(keyName)
        context.appPreferencesDataStore.edit { prefs ->
            prefs[key] = value
        }
    }

    /**
     * Read a string Flow for a key name with a default value.
     * Example: getStringFlow(context, "colleges_filter", "All")
     */
    fun getStringFlow(context: Context, keyName: String, defaultValue: String = ""): Flow<String> {
        val key = keyForName(keyName)
        return context.appPreferencesDataStore.data.map { prefs ->
            prefs[key] ?: defaultValue
        }
    }

    // Convenience wrappers used elsewhere
    suspend fun saveFilter(context: Context, keyName: String, filterValue: String) {
        saveString(context, keyName, filterValue)
    }

    suspend fun saveSearch(context: Context, keyName: String, searchValue: String) {
        saveString(context, keyName, searchValue)
    }
}
