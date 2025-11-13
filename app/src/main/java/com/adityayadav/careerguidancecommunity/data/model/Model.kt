package com.adityayadav.careerguidancecommunity.data.model

data class QuizAnswers(
    val favoriteSubject: String = "",
    val handsOnWork: String = "",
    val jobPreference: String = "",
    val workEnvironment: String = "",
    val leadership: String = "",
    val problemSolving: String = "",
    val creativity: String = "",
    val communication: String = ""
)

data class UserProgress(
    val points: Int = 0,
    val badges: List<String> = emptyList(),
    val completedQuiz: Boolean = false,
    val viewedResults: Boolean = false,
    val exploredColleges: Boolean = false,
    val checkedScholarships: Boolean = false
)
