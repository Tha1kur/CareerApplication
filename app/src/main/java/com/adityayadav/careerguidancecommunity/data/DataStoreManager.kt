package com.adityayadav.careerguidancecommunity.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.adityayadav.careerguidancecommunity.data.model.Profile
import com.adityayadav.careerguidancecommunity.data.model.UserProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "user_prefs"

// SINGLETON datastore extension (one per app). Use a unique name to avoid accidental duplicates.
private val Context.appPreferencesDataStore by preferencesDataStore(name = DATASTORE_NAME)

object DataStoreManager {

    // Profile keys
    private val KEY_PROFILE_NAME = stringPreferencesKey("profile_name")
    private val KEY_PROFILE_AGE = intPreferencesKey("profile_age")
    private val KEY_PROFILE_GOAL = stringPreferencesKey("profile_goal")

    // Generic string key helper
    private fun keyForName(name: String) = stringPreferencesKey(name)

    // --- Progress keys (used by QuizViewModel) ---
    private val KEY_POINTS = intPreferencesKey("points")
    private val KEY_BADGES = stringSetPreferencesKey("badges")
    private val KEY_COMPLETED = booleanPreferencesKey("completed_quiz")
    private val KEY_VIEWED_RESULTS = booleanPreferencesKey("viewed_results")
    private val KEY_EXPLORED_COLLEGES = booleanPreferencesKey("explored_colleges")
    private val KEY_CHECKED_SCHOLARSHIPS = booleanPreferencesKey("checked_scholarships")

    // ---------------- Profile helpers ----------------
    suspend fun saveProfile(context: Context, name: String, age: Int, goal: String) {
        context.appPreferencesDataStore.edit { prefs ->
            prefs[KEY_PROFILE_NAME] = name
            prefs[KEY_PROFILE_AGE] = age
            prefs[KEY_PROFILE_GOAL] = goal
        }
    }

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

    // ---------------- Generic string helpers ----------------
    suspend fun saveString(context: Context, keyName: String, value: String) {
        val key = keyForName(keyName)
        context.appPreferencesDataStore.edit { prefs ->
            prefs[key] = value
        }
    }

    fun getStringFlow(context: Context, keyName: String, defaultValue: String = ""): Flow<String> {
        val key = keyForName(keyName)
        return context.appPreferencesDataStore.data.map { prefs ->
            prefs[key] ?: defaultValue
        }
    }

    suspend fun saveFilter(context: Context, keyName: String, filterValue: String) {
        saveString(context, keyName, filterValue)
    }

    suspend fun saveSearch(context: Context, keyName: String, searchValue: String) {
        saveString(context, keyName, searchValue)
    }

    // ---------------- Progress helpers (used by QuizViewModel) ----------------

    /** Flow that emits current UserProgress built from stored prefs. */
    fun progressFlow(context: Context): Flow<UserProgress> {
        return context.appPreferencesDataStore.data.map { prefs ->
            UserProgress(
                points = prefs[KEY_POINTS] ?: 0,
                badges = prefs[KEY_BADGES]?.toList() ?: emptyList(),
                completedQuiz = prefs[KEY_COMPLETED] ?: false,
                viewedResults = prefs[KEY_VIEWED_RESULTS] ?: false,
                exploredColleges = prefs[KEY_EXPLORED_COLLEGES] ?: false,
                checkedScholarships = prefs[KEY_CHECKED_SCHOLARSHIPS] ?: false
            )
        }
    }

    /** Persist an entire UserProgress object. */
    suspend fun saveProgress(context: Context, p: UserProgress) {
        context.appPreferencesDataStore.edit { prefs ->
            prefs[KEY_POINTS] = p.points
            prefs[KEY_BADGES] = p.badges.toSet()
            prefs[KEY_COMPLETED] = p.completedQuiz
            prefs[KEY_VIEWED_RESULTS] = p.viewedResults
            prefs[KEY_EXPLORED_COLLEGES] = p.exploredColleges
            prefs[KEY_CHECKED_SCHOLARSHIPS] = p.checkedScholarships
        }
    }

    /** Convenience: add points */
    suspend fun addPoints(context: Context, delta: Int) {
        context.appPreferencesDataStore.edit { prefs ->
            val current = prefs[KEY_POINTS] ?: 0
            prefs[KEY_POINTS] = current + delta
        }
    }

    /** Convenience: add a badge */
    suspend fun addBadge(context: Context, badge: String) {
        context.appPreferencesDataStore.edit { prefs ->
            val set = prefs[KEY_BADGES]?.toMutableSet() ?: mutableSetOf()
            set.add(badge)
            prefs[KEY_BADGES] = set
        }
    }
}
