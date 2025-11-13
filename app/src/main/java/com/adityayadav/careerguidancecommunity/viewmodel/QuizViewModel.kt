package com.adityayadav.careerguidancecommunity.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import com.adityayadav.careerguidancecommunity.data.model.QuizAnswers
import com.adityayadav.careerguidancecommunity.data.model.UserProgress
import com.adityayadav.careerguidancecommunity.data.dataStore // not used — we access via application.dataStore
import com.adityayadav.careerguidancecommunity.data.dataStore as ds // safe alias (not necessary)

import com.adityayadav.careerguidancecommunity.data.dataStore
import android.content.Context

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    // DataStore keys
    companion object {
        private val POINTS_KEY = intPreferencesKey("points")
        private val BADGES_KEY = stringSetPreferencesKey("badges")
        private val COMPLETED_KEY = booleanPreferencesKey("completed_quiz")
        private val VIEWED_RESULTS_KEY = booleanPreferencesKey("viewed_results")
        private val EXPLORED_COLLEGES_KEY = booleanPreferencesKey("explored_colleges")
        private val CHECKED_SCHOLARSHIPS_KEY = booleanPreferencesKey("checked_scholarships")
    }

    private val ctx: Context = getApplication()

    // answers & scores unchanged from before
    private val _answers = MutableStateFlow(QuizAnswers())
    val answers: StateFlow<QuizAnswers> = _answers

    private val _scores = MutableStateFlow<Map<String, Int>>(emptyMap())
    val scores: StateFlow<Map<String, Int>> = _scores

    // progress backed by DataStore
    private val _progress = MutableStateFlow(UserProgress())
    val progress: StateFlow<UserProgress> = _progress

    init {
        // Load saved progress from DataStore into _progress
        viewModelScope.launch {
            ctx.dataStore.data.map { prefs ->
                UserProgress(
                    points = prefs[POINTS_KEY] ?: 0,
                    badges = prefs[BADGES_KEY]?.toList() ?: emptyList(),
                    completedQuiz = prefs[COMPLETED_KEY] ?: false,
                    viewedResults = prefs[VIEWED_RESULTS_KEY] ?: false,
                    exploredColleges = prefs[EXPLORED_COLLEGES_KEY] ?: false,
                    checkedScholarships = prefs[CHECKED_SCHOLARSHIPS_KEY] ?: false
                )
            }.collect { saved ->
                _progress.value = saved
            }
        }
    }

    // --- Answer updaters ---
    fun updateFavoriteSubject(value: String) { _answers.update { it.copy(favoriteSubject = value) } }
    fun updateHandsOnWork(value: String) { _answers.update { it.copy(handsOnWork = value) } }
    fun updateJobPreference(value: String) { _answers.update { it.copy(jobPreference = value) } }
    fun updateWorkEnvironment(value: String) { _answers.update { it.copy(workEnvironment = value) } }
    fun updateLeadership(value: String) { _answers.update { it.copy(leadership = value) } }
    fun updateProblemSolving(value: String) { _answers.update { it.copy(problemSolving = value) } }
    fun updateCreativity(value: String) { _answers.update { it.copy(creativity = value) } }
    fun updateCommunication(value: String) { _answers.update { it.copy(communication = value) } }

    // --- Scoring (same simple rules) ---
    fun computeScores(): Map<String, Int> {
        val a = _answers.value
        val scores = mutableMapOf(
            "Engineering" to 0,
            "Design" to 0,
            "Management" to 0,
            "Science" to 0
        )

        if (a.favoriteSubject.contains("math", ignoreCase = true) ||
            a.favoriteSubject.contains("physics", ignoreCase = true)
        ) {
            scores["Engineering"] = scores["Engineering"]!! + 10
            scores["Science"] = scores["Science"]!! + 6
        }
        if (a.favoriteSubject.contains("chemistry", ignoreCase = true) ||
            a.favoriteSubject.contains("biology", ignoreCase = true)
        ) scores["Science"] = scores["Science"]!! + 10

        if (a.creativity.contains("high", ignoreCase = true) ||
            a.favoriteSubject.contains("art", ignoreCase = true)
        ) scores["Design"] = scores["Design"]!! + 10

        if (a.leadership.contains("yes", ignoreCase = true) ||
            a.jobPreference.contains("manage", ignoreCase = true)
        ) scores["Management"] = scores["Management"]!! + 10

        if (a.handsOnWork.contains("yes", ignoreCase = true)) {
            scores["Engineering"] = scores["Engineering"]!! + 4
            scores["Design"] = scores["Design"]!! + 4
        }
        if (a.problemSolving.contains("high", ignoreCase = true)) {
            scores["Engineering"] = scores["Engineering"]!! + 6
            scores["Science"] = scores["Science"]!! + 4
        }

        _scores.value = scores
        return scores
    }

    fun submitQuiz(): Map<String, Int> {
        val computed = computeScores()
        // update progress flags
        addPoints(100) // reward for finishing quiz
        updateProgress { it.copy(completedQuiz = true, viewedResults = true) }
        addBadge("Quiz Master")
        // persist after updating
        persistProgress()
        return computed
    }

    // --- Progress helpers ---
    private fun updateProgress(update: (UserProgress) -> UserProgress) {
        _progress.update { update(it) }
        // persist each time we update
        persistProgress()
    }

    fun addPoints(value: Int) {
        _progress.update { prev -> prev.copy(points = prev.points + value) }
        persistProgress()
    }

    fun addBadge(badge: String) {
        _progress.update { prev ->
            if (prev.badges.contains(badge)) prev
            else prev.copy(badges = prev.badges + badge)
        }
        persistProgress()
    }

    fun markViewedResults() {
        updateProgress { it.copy(viewedResults = true) }
    }

    fun markExploredColleges() {
        updateProgress { it.copy(exploredColleges = true) }
    }

    fun markCheckedScholarships() {
        updateProgress { it.copy(checkedScholarships = true) }
    }

    // --- DataStore persistence ---
    private fun persistProgress() {
        val p = _progress.value
        viewModelScope.launch {
            ctx.dataStore.edit { prefs ->
                prefs[POINTS_KEY] = p.points
                prefs[BADGES_KEY] = p.badges.toSet()
                prefs[COMPLETED_KEY] = p.completedQuiz
                prefs[VIEWED_RESULTS_KEY] = p.viewedResults
                prefs[EXPLORED_COLLEGES_KEY] = p.exploredColleges
                prefs[CHECKED_SCHOLARSHIPS_KEY] = p.checkedScholarships
            }
        }
    }
}
