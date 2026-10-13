package com.adityayadav.careerguidancecommunity

import com.adityayadav.careerguidancecommunity.data.model.QuizAnswers
import com.adityayadav.careerguidancecommunity.data.model.calculateQuizScores
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizScoringFunctionalTest {

    @Test
    fun `empty answers returns zero for all domains`() {
        val scores = calculateQuizScores(QuizAnswers())
        assertEquals(0, scores["Engineering"])
        assertEquals(0, scores["Design"])
        assertEquals(0, scores["Management"])
        assertEquals(0, scores["Science"])
        assertEquals(0, scores["Entrepreneurship"])
    }

    @Test
    fun `engineering profile yields top score for Engineering`() {
        val answers = QuizAnswers(
            favoriteSubject = "math",
            handsOnWork = "yes",
            jobPreference = "private",
            workEnvironment = "mixed",
            leadership = "somewhat",
            problemSolving = "analytical",
            creativity = "somewhat",
            communication = "written"
        )
        val scores = calculateQuizScores(answers)
        val topTrack = scores.maxByOrNull { it.value }?.key
        assertEquals("Engineering", topTrack)
        assertTrue(scores["Engineering"]!! > scores["Design"]!!)
        assertTrue(scores["Engineering"]!! > scores["Management"]!!)
    }

    @Test
    fun `creative design profile yields top score for Design`() {
        val answers = QuizAnswers(
            favoriteSubject = "arts",
            handsOnWork = "yes",
            jobPreference = "private",
            workEnvironment = "independent",
            leadership = "not",
            problemSolving = "creative",
            creativity = "very",
            communication = "written"
        )
        val scores = calculateQuizScores(answers)
        val topTrack = scores.maxByOrNull { it.value }?.key
        assertEquals("Design", topTrack)
        assertTrue(scores["Design"]!! > scores["Engineering"]!!)
    }

    @Test
    fun `business and entrepreneurship profile yields high Entrepreneurship and Management`() {
        val answers = QuizAnswers(
            favoriteSubject = "commerce",
            handsOnWork = "no",
            jobPreference = "business",
            workEnvironment = "team",
            leadership = "very",
            problemSolving = "people",
            creativity = "somewhat",
            communication = "public"
        )
        val scores = calculateQuizScores(answers)
        val topTrack = scores.maxByOrNull { it.value }?.key
        assertTrue(topTrack == "Entrepreneurship" || topTrack == "Management")
        assertTrue(scores["Entrepreneurship"]!! >= 25)
        assertTrue(scores["Management"]!! >= 25)
    }

    @Test
    fun `science research profile yields top score for Science`() {
        val answers = QuizAnswers(
            favoriteSubject = "science",
            handsOnWork = "no",
            jobPreference = "government",
            workEnvironment = "independent",
            leadership = "not",
            problemSolving = "analytical",
            creativity = "not",
            communication = "written"
        )
        val scores = calculateQuizScores(answers)
        val topTrack = scores.maxByOrNull { it.value }?.key
        assertEquals("Science", topTrack)
    }
}
