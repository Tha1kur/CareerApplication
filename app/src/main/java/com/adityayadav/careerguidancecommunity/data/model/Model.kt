package com.adityayadav.careerguidancecommunity.data.model

import kotlinx.serialization.Serializable

@Serializable
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

data class LevelInfo(
    val level: Int,
    val progressFraction: Float,
    val currentXp: Int = 0,
    val nextLevelXp: Int = 0
)

fun calculateLevel(points: Int): LevelInfo {
    val xpPerLevel = 150
    val level = (points / xpPerLevel) + 1
    val currentXp = points % xpPerLevel
    val fraction = (currentXp.toFloat() / xpPerLevel.toFloat()).coerceIn(0f, 1f)
    return LevelInfo(
        level = level,
        progressFraction = fraction,
        currentXp = currentXp,
        nextLevelXp = xpPerLevel
    )
}

fun calculateQuizScores(a: QuizAnswers): Map<String, Int> {
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

    // Hands-on preference
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

    // Type of problem-solving
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

    return scores
}

@Serializable
data class QuizAttempt(
    val id: String,
    val timestamp: Long,
    val formattedDate: String,
    val topCareer: String,
    val topScore: Int,
    val scores: Map<String, Int> = emptyMap()
)

data class CollegeComparisonItem(
    val college: College,
    val isBestRank: Boolean = false,
    val isLowestFee: Boolean = false
)

// ========================================================
// PHASE 3 NEXT-GEN MODELS: AI MENTOR, TRACKER & COMMUNITY
// ========================================================

@Serializable
data class AiCareerMilestone(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val priority: String = "High",
    val estimatedTimeline: String = "1-3 Months",
    val actionRoute: String? = null
)

@Serializable
data class AiCareerPlan(
    val careerTitle: String,
    val careerCategory: String,
    val matchPercentage: Int,
    val executiveSummary: String,
    val keyStrengths: List<String>,
    val growthAreas: List<String>,
    val targetEntranceExams: List<String>,
    val recommendedSkills: List<String>,
    val portfolioProjects: List<String>,
    val milestones: List<AiCareerMilestone>
)

@Serializable
enum class ApplicationStage {
    DISCOVERED,
    DOCS_READY,
    APPLIED,
    AWARDED
}

@Serializable
data class TrackedScholarship(
    val scholarshipId: String,
    val scholarshipName: String,
    val provider: String,
    val amount: String,
    val deadline: String,
    val stage: ApplicationStage = ApplicationStage.DISCOVERED,
    val completedDocuments: Set<String> = emptySet(),
    val notes: String = "",
    val updatedAt: Long = 0L
)

@Serializable
data class CommunityAnswer(
    val id: String,
    val author: String,
    val authorBadge: String = "",
    val isVerifiedMentor: Boolean = false,
    val content: String,
    val timestamp: Long,
    val upvotes: Int = 0
)

@Serializable
data class CommunityQuestion(
    val id: String,
    val title: String,
    val author: String,
    val authorRole: String = "",
    val category: String,
    val content: String,
    val timestamp: Long,
    val upvotes: Int = 0,
    val answers: List<CommunityAnswer> = emptyList(),
    val tags: List<String> = emptyList()
)

