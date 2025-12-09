package com.adityayadav.careerguidancecommunity.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adityayadav.careerguidancecommunity.data.DataStoreManager
import com.adityayadav.careerguidancecommunity.data.model.QuizAnswers
import com.adityayadav.careerguidancecommunity.data.model.UserProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val ctx = application.applicationContext

    // Answers from aptitude quiz
    private val _answers = MutableStateFlow(QuizAnswers())
    val answers: StateFlow<QuizAnswers> = _answers

    // Career scores: Engineering, Design, Management, Science, Entrepreneurship
    private val _scores = MutableStateFlow<Map<String, Int>>(emptyMap())
    val scores: StateFlow<Map<String, Int>> = _scores

    // User progress (points, badges, milestones)
    private val _progress = MutableStateFlow(UserProgress())
    val progress: StateFlow<UserProgress> = _progress

    init {
        // Collect persisted progress from DataStore
        viewModelScope.launch {
            DataStoreManager.progressFlow(ctx).collect { saved ->
                _progress.value = saved
            }
        }
    }

    // --- Answer updaters -----------------------------------------------------

    fun updateFavoriteSubject(value: String) {
        _answers.update { it.copy(favoriteSubject = value) }
    }

    fun updateHandsOnWork(value: String) {
        _answers.update { it.copy(handsOnWork = value) }
    }

    fun updateJobPreference(value: String) {
        _answers.update { it.copy(jobPreference = value) }
    }

    fun updateWorkEnvironment(value: String) {
        _answers.update { it.copy(workEnvironment = value) }
    }

    fun updateLeadership(value: String) {
        _answers.update { it.copy(leadership = value) }
    }

    fun updateProblemSolving(value: String) {
        _answers.update { it.copy(problemSolving = value) }
    }

    fun updateCreativity(value: String) {
        _answers.update { it.copy(creativity = value) }
    }

    fun updateCommunication(value: String) {
        _answers.update { it.copy(communication = value) }
    }

    // --- Scoring based on aptitudequiz.json values ---------------------------
    // Values like: math, science, arts, commerce, yes/no/maybe, etc.  [oai_citation:0‡aptitudequiz.json](file-service://file-FCWAnwGvakkKZN3j9yPhzC)
    fun computeScores(): Map<String, Int> {
        val a = _answers.value

        val scores = mutableMapOf(
            "Engineering" to 0,
            "Design" to 0,
            "Management" to 0,
            "Science" to 0,
            "Entrepreneurship" to 0
        )

        // Favorite subject
        when (a.favoriteSubject.lowercase()) {
            "math" -> {
                scores["Engineering"] = scores["Engineering"]!! + 10
                scores["Management"] = scores["Management"]!! + 4
            }
            "science" -> {
                scores["Science"] = scores["Science"]!! + 10
                scores["Engineering"] = scores["Engineering"]!! + 4
            }
            "arts" -> {
                scores["Design"] = scores["Design"]!! + 10
            }
            "commerce" -> {
                scores["Management"] = scores["Management"]!! + 8
                scores["Entrepreneurship"] = scores["Entrepreneurship"]!! + 8
            }
        }

        // Hands‑on preference
        when (a.handsOnWork.lowercase()) {
            "yes" -> {
                scores["Engineering"] = scores["Engineering"]!! + 4
                scores["Design"] = scores["Design"]!! + 4
                scores["Science"] = scores["Science"]!! + 2
            }
            "no" -> {
                scores["Science"] = scores["Science"]!! + 3
                scores["Management"] = scores["Management"]!! + 2
            }
            "maybe" -> {
                scores["Engineering"] = scores["Engineering"]!! + 1
                scores["Design"] = scores["Design"]!! + 1
                scores["Science"] = scores["Science"]!! + 1
            }
        }

        // Job preference
        when (a.jobPreference.lowercase()) {
            "government" -> {
                scores["Science"] = scores["Science"]!! + 4
                scores["Management"] = scores["Management"]!! + 4
            }
            "private" -> {
                scores["Engineering"] = scores["Engineering"]!! + 3
                scores["Design"] = scores["Design"]!! + 3
                scores["Management"] = scores["Management"]!! + 3
            }
            "business" -> {
                scores["Entrepreneurship"] = scores["Entrepreneurship"]!! + 10
                scores["Management"] = scores["Management"]!! + 5
            }
        }

        // Work environment
        when (a.workEnvironment.lowercase()) {
            "team" -> {
                scores["Management"] = scores["Management"]!! + 4
                scores["Entrepreneurship"] = scores["Entrepreneurship"]!! + 3
            }
            "independent" -> {
                scores["Design"] = scores["Design"]!! + 3
                scores["Science"] = scores["Science"]!! + 3
            }
            "mixed" -> {
                scores["Engineering"] = scores["Engineering"]!! + 2
                scores["Management"] = scores["Management"]!! + 2
                scores["Entrepreneurship"] = scores["Entrepreneurship"]!! + 2
            }
        }

        // Leadership comfort
        when (a.leadership.lowercase()) {
            "very" -> {
                scores["Management"] = scores["Management"]!! + 5
                scores["Entrepreneurship"] = scores["Entrepreneurship"]!! + 5
            }
            "somewhat" -> {
                scores["Management"] = scores["Management"]!! + 3
                scores["Entrepreneurship"] = scores["Entrepreneurship"]!! + 3
            }
            "not" -> {
                scores["Design"] = scores["Design"]!! + 2
                scores["Science"] = scores["Science"]!! + 2
            }
        }

        // Type of problem‑solving
        when (a.problemSolving.lowercase()) {
            "analytical" -> {
                scores["Engineering"] = scores["Engineering"]!! + 5
                scores["Science"] = scores["Science"]!! + 5
            }
            "creative" -> {
                scores["Design"] = scores["Design"]!! + 6
                scores["Entrepreneurship"] = scores["Entrepreneurship"]!! + 2
            }
            "people" -> {
                scores["Management"] = scores["Management"]!! + 5
                scores["Entrepreneurship"] = scores["Entrepreneurship"]!! + 4
            }
        }

        // Creativity importance
        when (a.creativity.lowercase()) {
            "very" -> {
                scores["Design"] = scores["Design"]!! + 6
                scores["Entrepreneurship"] = scores["Entrepreneurship"]!! + 4
            }
            "somewhat" -> {
                scores["Design"] = scores["Design"]!! + 3
                scores["Engineering"] = scores["Engineering"]!! + 2
            }
            "not" -> {
                scores["Engineering"] = scores["Engineering"]!! + 3
                scores["Science"] = scores["Science"]!! + 3
            }
        }

        // Communication style
        when (a.communication.lowercase()) {
            "public" -> {
                scores["Management"] = scores["Management"]!! + 4
                scores["Entrepreneurship"] = scores["Entrepreneurship"]!! + 4
            }
            "written" -> {
                scores["Science"] = scores["Science"]!! + 3
                scores["Design"] = scores["Design"]!! + 3
            }
            "oneonone" -> {
                scores["Management"] = scores["Management"]!! + 3
                scores["Entrepreneurship"] = scores["Entrepreneurship"]!! + 2
                scores["Design"] = scores["Design"]!! + 2
            }
        }

        _scores.value = scores
        return scores
    }

    // Called when the user taps "Submit" in the quiz
    fun submitQuiz(): Map<String, Int> {
        val computed = computeScores()
        // Reward finishing the quiz
        addPoints(100)
        updateProgress { it.copy(completedQuiz = true, viewedResults = true) }
        addBadge("Quiz Master")
        return computed
    }

    // --- Progress helpers ----------------------------------------------------

    private fun updateProgress(update: (UserProgress) -> UserProgress) {
        _progress.update { prev -> update(prev) }
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

    /**
     * Called when user saves a career path from the recommendations screen.
     * Gives a one‑time +50 XP bonus and a "Career Path Selected" badge.
     */
    fun onCareerPathSaved() {
        val alreadyHasBadge = _progress.value.badges.contains("Career Path Selected")
        if (!alreadyHasBadge) {
            addPoints(50)
            addBadge("Career Path Selected")
        }
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

    /** Reset only gamification progress (keep answers & scores as-is if you want) */
    fun resetProgress() {
        _progress.value = UserProgress()
        persistProgress()
    }

    /** Reset everything: progress + answers + scores */
    fun resetAllProgress() {
        _progress.value = UserProgress()
        persistProgress()
        _answers.value = QuizAnswers()
        _scores.value = emptyMap()
    }

    // Persist the current progress to DataStore via DataStoreManager
    private fun persistProgress() {
        val p = _progress.value
        viewModelScope.launch {
            try {
                DataStoreManager.saveProgress(ctx, p)
            } catch (e: Exception) {
                // avoid crashing the app if datastore write fails; log instead
                e.printStackTrace()
            }
        }
    }
}