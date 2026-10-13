package com.adityayadav.careerguidancecommunity.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.adityayadav.careerguidancecommunity.data.model.Profile
import com.adityayadav.careerguidancecommunity.data.model.QuizAnswers
import com.adityayadav.careerguidancecommunity.data.model.QuizAttempt
import com.adityayadav.careerguidancecommunity.data.model.UserProgress
import com.adityayadav.careerguidancecommunity.data.model.ApplicationStage
import com.adityayadav.careerguidancecommunity.data.model.TrackedScholarship
import com.adityayadav.careerguidancecommunity.data.model.CommunityQuestion
import com.adityayadav.careerguidancecommunity.data.model.CommunityAnswer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

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

    // --- Quiz Answers & Scores persistence ---
    private val KEY_QUIZ_ANSWERS = stringPreferencesKey("quiz_answers_json")
    private val KEY_QUIZ_SCORES = stringPreferencesKey("quiz_scores_json")

    private val jsonSerializer = Json { ignoreUnknownKeys = true }

    suspend fun saveQuizAnswers(context: Context, answers: QuizAnswers) {
        context.appPreferencesDataStore.edit { prefs ->
            prefs[KEY_QUIZ_ANSWERS] = jsonSerializer.encodeToString(answers)
        }
    }

    fun quizAnswersFlow(context: Context): Flow<QuizAnswers> {
        return context.appPreferencesDataStore.data.map { prefs ->
            val raw = prefs[KEY_QUIZ_ANSWERS] ?: ""
            if (raw.isNotBlank()) {
                try {
                    jsonSerializer.decodeFromString<QuizAnswers>(raw)
                } catch (e: Exception) {
                    QuizAnswers()
                }
            } else {
                QuizAnswers()
            }
        }
    }

    suspend fun saveQuizScores(context: Context, scores: Map<String, Int>) {
        context.appPreferencesDataStore.edit { prefs ->
            prefs[KEY_QUIZ_SCORES] = jsonSerializer.encodeToString(scores)
        }
    }

    fun quizScoresFlow(context: Context): Flow<Map<String, Int>> {
        return context.appPreferencesDataStore.data.map { prefs ->
            val raw = prefs[KEY_QUIZ_SCORES] ?: ""
            if (raw.isNotBlank()) {
                try {
                    jsonSerializer.decodeFromString<Map<String, Int>>(raw)
                } catch (e: Exception) {
                    emptyMap()
                }
            } else {
                emptyMap()
            }
        }
    }

    suspend fun clearQuizData(context: Context) {
        context.appPreferencesDataStore.edit { prefs ->
            prefs.remove(KEY_QUIZ_ANSWERS)
            prefs.remove(KEY_QUIZ_SCORES)
        }
    }

    // --- Saved/Bookmarked Opportunities ---
    private val KEY_SAVED_COLLEGES = stringSetPreferencesKey("saved_colleges")
    private val KEY_SAVED_SCHOLARSHIPS = stringSetPreferencesKey("saved_scholarships")

    fun savedCollegesFlow(context: Context): Flow<Set<String>> {
        return context.appPreferencesDataStore.data.map { prefs ->
            prefs[KEY_SAVED_COLLEGES] ?: emptySet()
        }
    }

    suspend fun toggleSaveCollege(context: Context, collegeId: String) {
        context.appPreferencesDataStore.edit { prefs ->
            val set = prefs[KEY_SAVED_COLLEGES]?.toMutableSet() ?: mutableSetOf()
            if (set.contains(collegeId)) {
                set.remove(collegeId)
            } else {
                set.add(collegeId)
            }
            prefs[KEY_SAVED_COLLEGES] = set
        }
    }

    suspend fun setCollegeSaved(context: Context, collegeId: String, isSaved: Boolean) {
        context.appPreferencesDataStore.edit { prefs ->
            val set = prefs[KEY_SAVED_COLLEGES]?.toMutableSet() ?: mutableSetOf()
            if (isSaved) set.add(collegeId) else set.remove(collegeId)
            prefs[KEY_SAVED_COLLEGES] = set
        }
    }

    fun savedScholarshipsFlow(context: Context): Flow<Set<String>> {
        return context.appPreferencesDataStore.data.map { prefs ->
            prefs[KEY_SAVED_SCHOLARSHIPS] ?: emptySet()
        }
    }

    suspend fun toggleSaveScholarship(context: Context, scholarshipId: String) {
        context.appPreferencesDataStore.edit { prefs ->
            val set = prefs[KEY_SAVED_SCHOLARSHIPS]?.toMutableSet() ?: mutableSetOf()
            if (set.contains(scholarshipId)) {
                set.remove(scholarshipId)
            } else {
                set.add(scholarshipId)
            }
            prefs[KEY_SAVED_SCHOLARSHIPS] = set
        }
    }

    suspend fun setScholarshipSaved(context: Context, scholarshipId: String, isSaved: Boolean) {
        context.appPreferencesDataStore.edit { prefs ->
            val set = prefs[KEY_SAVED_SCHOLARSHIPS]?.toMutableSet() ?: mutableSetOf()
            if (isSaved) set.add(scholarshipId) else set.remove(scholarshipId)
            prefs[KEY_SAVED_SCHOLARSHIPS] = set
        }
    }

    // --- Quiz Attempts History ---
    private val KEY_QUIZ_HISTORY = stringPreferencesKey("quiz_history_json")

    fun quizHistoryFlow(context: Context): Flow<List<QuizAttempt>> {
        return context.appPreferencesDataStore.data.map { prefs ->
            val raw = prefs[KEY_QUIZ_HISTORY] ?: ""
            if (raw.isNotBlank()) {
                try {
                    jsonSerializer.decodeFromString<List<QuizAttempt>>(raw)
                } catch (e: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }
        }
    }

    suspend fun recordQuizAttempt(context: Context, attempt: QuizAttempt) {
        context.appPreferencesDataStore.edit { prefs ->
            val raw = prefs[KEY_QUIZ_HISTORY] ?: ""
            val list = if (raw.isNotBlank()) {
                try {
                    jsonSerializer.decodeFromString<List<QuizAttempt>>(raw).toMutableList()
                } catch (e: Exception) {
                    mutableListOf()
                }
            } else {
                mutableListOf()
            }
            list.add(0, attempt)
            prefs[KEY_QUIZ_HISTORY] = jsonSerializer.encodeToString(list)
        }
    }

    suspend fun clearQuizHistory(context: Context) {
        context.appPreferencesDataStore.edit { prefs ->
            prefs.remove(KEY_QUIZ_HISTORY)
        }
    }

    // --- Phase 3: AI Career Milestones ---
    private val KEY_COMPLETED_MILESTONES = stringSetPreferencesKey("completed_milestones")

    fun completedMilestonesFlow(context: Context): Flow<Set<String>> {
        return context.appPreferencesDataStore.data.map { prefs ->
            prefs[KEY_COMPLETED_MILESTONES] ?: emptySet()
        }
    }

    suspend fun toggleMilestone(context: Context, milestoneId: String) {
        context.appPreferencesDataStore.edit { prefs ->
            val set = prefs[KEY_COMPLETED_MILESTONES]?.toMutableSet() ?: mutableSetOf()
            if (set.contains(milestoneId)) {
                set.remove(milestoneId)
            } else {
                set.add(milestoneId)
            }
            prefs[KEY_COMPLETED_MILESTONES] = set
        }
    }

    suspend fun setMilestoneCompleted(context: Context, milestoneId: String, isCompleted: Boolean) {
        context.appPreferencesDataStore.edit { prefs ->
            val set = prefs[KEY_COMPLETED_MILESTONES]?.toMutableSet() ?: mutableSetOf()
            if (isCompleted) set.add(milestoneId) else set.remove(milestoneId)
            prefs[KEY_COMPLETED_MILESTONES] = set
        }
    }

    // --- Phase 3: Scholarship Application Tracker ---
    private val KEY_TRACKED_SCHOLARSHIPS = stringPreferencesKey("tracked_scholarships_json")

    fun trackedScholarshipsFlow(context: Context): Flow<List<TrackedScholarship>> {
        return context.appPreferencesDataStore.data.map { prefs ->
            val raw = prefs[KEY_TRACKED_SCHOLARSHIPS] ?: ""
            if (raw.isNotBlank()) {
                try {
                    jsonSerializer.decodeFromString<List<TrackedScholarship>>(raw)
                } catch (e: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }
        }
    }

    suspend fun trackScholarship(context: Context, item: TrackedScholarship) {
        context.appPreferencesDataStore.edit { prefs ->
            val raw = prefs[KEY_TRACKED_SCHOLARSHIPS] ?: ""
            val list = if (raw.isNotBlank()) {
                try {
                    jsonSerializer.decodeFromString<List<TrackedScholarship>>(raw).toMutableList()
                } catch (e: Exception) {
                    mutableListOf()
                }
            } else {
                mutableListOf()
            }
            val existingIndex = list.indexOfFirst { it.scholarshipId == item.scholarshipId }
            if (existingIndex >= 0) {
                list[existingIndex] = item
            } else {
                list.add(item)
            }
            prefs[KEY_TRACKED_SCHOLARSHIPS] = jsonSerializer.encodeToString(list)
        }
    }

    suspend fun updateScholarshipStage(context: Context, scholarshipId: String, newStage: ApplicationStage) {
        context.appPreferencesDataStore.edit { prefs ->
            val raw = prefs[KEY_TRACKED_SCHOLARSHIPS] ?: ""
            if (raw.isNotBlank()) {
                val list = try {
                    jsonSerializer.decodeFromString<List<TrackedScholarship>>(raw).toMutableList()
                } catch (e: Exception) {
                    mutableListOf()
                }
                val index = list.indexOfFirst { it.scholarshipId == scholarshipId }
                if (index >= 0) {
                    val current = list[index]
                    list[index] = current.copy(stage = newStage, updatedAt = System.currentTimeMillis())
                    prefs[KEY_TRACKED_SCHOLARSHIPS] = jsonSerializer.encodeToString(list)
                }
            }
        }
    }

    suspend fun toggleScholarshipDocument(context: Context, scholarshipId: String, docName: String) {
        context.appPreferencesDataStore.edit { prefs ->
            val raw = prefs[KEY_TRACKED_SCHOLARSHIPS] ?: ""
            if (raw.isNotBlank()) {
                val list = try {
                    jsonSerializer.decodeFromString<List<TrackedScholarship>>(raw).toMutableList()
                } catch (e: Exception) {
                    mutableListOf()
                }
                val index = list.indexOfFirst { it.scholarshipId == scholarshipId }
                if (index >= 0) {
                    val current = list[index]
                    val updatedDocs = current.completedDocuments.toMutableSet()
                    if (updatedDocs.contains(docName)) {
                        updatedDocs.remove(docName)
                    } else {
                        updatedDocs.add(docName)
                    }
                    list[index] = current.copy(completedDocuments = updatedDocs, updatedAt = System.currentTimeMillis())
                    prefs[KEY_TRACKED_SCHOLARSHIPS] = jsonSerializer.encodeToString(list)
                }
            }
        }
    }

    suspend fun saveScholarshipNotes(context: Context, scholarshipId: String, notes: String) {
        context.appPreferencesDataStore.edit { prefs ->
            val raw = prefs[KEY_TRACKED_SCHOLARSHIPS] ?: ""
            if (raw.isNotBlank()) {
                val list = try {
                    jsonSerializer.decodeFromString<List<TrackedScholarship>>(raw).toMutableList()
                } catch (e: Exception) {
                    mutableListOf()
                }
                val index = list.indexOfFirst { it.scholarshipId == scholarshipId }
                if (index >= 0) {
                    val current = list[index]
                    list[index] = current.copy(notes = notes, updatedAt = System.currentTimeMillis())
                    prefs[KEY_TRACKED_SCHOLARSHIPS] = jsonSerializer.encodeToString(list)
                }
            }
        }
    }

    suspend fun untrackScholarship(context: Context, scholarshipId: String) {
        context.appPreferencesDataStore.edit { prefs ->
            val raw = prefs[KEY_TRACKED_SCHOLARSHIPS] ?: ""
            if (raw.isNotBlank()) {
                val list = try {
                    jsonSerializer.decodeFromString<List<TrackedScholarship>>(raw).toMutableList()
                } catch (e: Exception) {
                    mutableListOf()
                }
                list.removeAll { it.scholarshipId == scholarshipId }
                prefs[KEY_TRACKED_SCHOLARSHIPS] = jsonSerializer.encodeToString(list)
            }
        }
    }

    // --- Phase 3: Peer Community & Mentorship Q&A Hub ---
    private val KEY_COMMUNITY_QUESTIONS = stringPreferencesKey("community_questions_json")
    private val KEY_UPVOTED_QUESTIONS = stringSetPreferencesKey("upvoted_questions")

    fun communityQuestionsFlow(context: Context): Flow<List<CommunityQuestion>> {
        return context.appPreferencesDataStore.data.map { prefs ->
            val raw = prefs[KEY_COMMUNITY_QUESTIONS] ?: ""
            if (raw.isNotBlank()) {
                try {
                    jsonSerializer.decodeFromString<List<CommunityQuestion>>(raw)
                } catch (e: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }
        }
    }

    suspend fun saveCommunityQuestions(context: Context, questions: List<CommunityQuestion>) {
        context.appPreferencesDataStore.edit { prefs ->
            prefs[KEY_COMMUNITY_QUESTIONS] = jsonSerializer.encodeToString(questions)
        }
    }

    fun upvotedQuestionsFlow(context: Context): Flow<Set<String>> {
        return context.appPreferencesDataStore.data.map { prefs ->
            prefs[KEY_UPVOTED_QUESTIONS] ?: emptySet()
        }
    }

    suspend fun toggleQuestionUpvote(context: Context, questionId: String) {
        context.appPreferencesDataStore.edit { prefs ->
            val set = prefs[KEY_UPVOTED_QUESTIONS]?.toMutableSet() ?: mutableSetOf()
            if (set.contains(questionId)) {
                set.remove(questionId)
            } else {
                set.add(questionId)
            }
            prefs[KEY_UPVOTED_QUESTIONS] = set
        }
    }
}
