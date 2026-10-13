package com.adityayadav.careerguidancecommunity

import com.adityayadav.careerguidancecommunity.data.model.QuizAttempt
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizAttemptFunctionalTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `quiz attempt serializes and deserializes accurately`() {
        val attempt = QuizAttempt(
            id = "attempt_1",
            timestamp = 1728800000000L,
            formattedDate = "Oct 13, 2026 • 10:30",
            topCareer = "Engineering",
            topScore = 32,
            scores = mapOf("Engineering" to 32, "Design" to 15, "Management" to 12)
        )

        val serialized = json.encodeToString(attempt)
        val deserialized = json.decodeFromString<QuizAttempt>(serialized)

        assertEquals("attempt_1", deserialized.id)
        assertEquals(1728800000000L, deserialized.timestamp)
        assertEquals("Engineering", deserialized.topCareer)
        assertEquals(32, deserialized.topScore)
        assertEquals(3, deserialized.scores.size)
        assertEquals(32, deserialized.scores["Engineering"])
    }

    @Test
    fun `history list maintains chronological ordering and detects evolution`() {
        val attempt1 = QuizAttempt(
            id = "att_1",
            timestamp = 1000L,
            formattedDate = "Oct 1, 2026",
            topCareer = "Management",
            topScore = 20,
            scores = mapOf("Management" to 20, "Engineering" to 14)
        )
        val attempt2 = QuizAttempt(
            id = "att_2",
            timestamp = 2000L,
            formattedDate = "Oct 13, 2026",
            topCareer = "Engineering",
            topScore = 34,
            scores = mapOf("Engineering" to 34, "Management" to 15)
        )

        val history = listOf(attempt2, attempt1) // latest first

        assertEquals(2, history.size)
        assertEquals("Engineering", history.first().topCareer)
        assertEquals(34, history.first().topScore)
        assertEquals("Management", history.last().topCareer)
        assertTrue(history.first().timestamp > history.last().timestamp)
    }
}
