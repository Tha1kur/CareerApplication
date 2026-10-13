package com.adityayadav.careerguidancecommunity.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adityayadav.careerguidancecommunity.data.DataStoreManager
import com.adityayadav.careerguidancecommunity.data.model.QuizAnswers
import com.adityayadav.careerguidancecommunity.data.model.QuizAttempt
import com.adityayadav.careerguidancecommunity.data.model.UserProgress
import com.adityayadav.careerguidancecommunity.data.model.calculateQuizScores
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    // Past quiz attempts history
    private val _quizHistory = MutableStateFlow<List<QuizAttempt>>(emptyList())
    val quizHistory: StateFlow<List<QuizAttempt>> = _quizHistory

    init {
        // Collect persisted progress, answers, and scores from DataStore
        viewModelScope.launch {
            DataStoreManager.progressFlow(ctx).collect { saved ->
                _progress.value = saved
            }
        }
        viewModelScope.launch {
            DataStoreManager.quizAnswersFlow(ctx).collect { savedAnswers ->
                _answers.value = savedAnswers
            }
        }
        viewModelScope.launch {
            DataStoreManager.quizScoresFlow(ctx).collect { savedScores ->
                _scores.value = savedScores
            }
        }
        viewModelScope.launch {
            DataStoreManager.quizHistoryFlow(ctx).collect { savedHistory ->
                _quizHistory.value = savedHistory
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
    fun computeScores(): Map<String, Int> {
        val scores = calculateQuizScores(_answers.value)
        _scores.value = scores
        return scores
    }

    // Called when the user taps "Submit" in the quiz
    fun submitQuiz(): Map<String, Int> {
        val computed = computeScores()
        val currentAnswers = _answers.value

        val topCareer = computed.maxByOrNull { it.value }?.key ?: "General"
        val topScore = computed[topCareer] ?: 0
        val attempt = QuizAttempt(
            id = System.currentTimeMillis().toString(),
            timestamp = System.currentTimeMillis(),
            formattedDate = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()).format(Date()),
            topCareer = topCareer,
            topScore = topScore,
            scores = computed
        )

        viewModelScope.launch {
            try {
                DataStoreManager.saveQuizAnswers(ctx, currentAnswers)
                DataStoreManager.saveQuizScores(ctx, computed)
                DataStoreManager.recordQuizAttempt(ctx, attempt)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        _progress.update { prev ->
            val updatedBadges = if (prev.badges.contains("Quiz Master")) prev.badges else prev.badges + "Quiz Master"
            prev.copy(
                points = prev.points + 100,
                completedQuiz = true,
                viewedResults = true,
                badges = updatedBadges
            )
        }
        persistProgress()
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
        viewModelScope.launch {
            try {
                DataStoreManager.clearQuizData(ctx)
                DataStoreManager.clearQuizHistory(ctx)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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