package com.adityayadav.careerguidancecommunity

import com.adityayadav.careerguidancecommunity.data.model.CommunityAnswer
import com.adityayadav.careerguidancecommunity.data.model.CommunityQuestion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CommunityForumFunctionalTest {

    private val sampleQuestions = listOf(
        CommunityQuestion(
            id = "q_1",
            title = "How to balance JEE prep with learning Android?",
            author = "Rohit",
            authorRole = "Class 12 Aspirant",
            category = "Technology",
            content = "I want to code Kotlin while studying for IIT JEE.",
            timestamp = 1715000000000L,
            upvotes = 40,
            tags = listOf("JEE", "Android", "Kotlin"),
            answers = listOf(
                CommunityAnswer(
                    id = "a_1",
                    author = "Arun",
                    authorBadge = "IIT Alum",
                    isVerifiedMentor = true,
                    content = "Prioritize JEE for now and code on Sundays.",
                    timestamp = 1715050000000L,
                    upvotes = 35
                )
            )
        ),
        CommunityQuestion(
            id = "q_2",
            title = "Is a degree from NID required for UI/UX?",
            author = "Tanvi",
            authorRole = "Design Aspirant",
            category = "Design",
            content = "Can self taught product designers get hired in top tech?",
            timestamp = 1715100000000L,
            upvotes = 25,
            tags = listOf("Design", "Figma", "NID"),
            answers = emptyList()
        ),
        CommunityQuestion(
            id = "q_3",
            title = "Best undergraduate stream for IIM Ahmedabad MBA?",
            author = "Karan",
            authorRole = "Commerce Student",
            category = "Management",
            content = "Should I do B.Com or Engineering for CAT?",
            timestamp = 1715200000000L,
            upvotes = 50,
            tags = listOf("CAT", "IIM", "MBA"),
            answers = emptyList()
        )
    )

    @Test
    fun testCategoryFiltering() {
        val techOnly = sampleQuestions.filter { it.category == "Technology" }
        assertEquals(1, techOnly.size)
        assertEquals("q_1", techOnly[0].id)

        val designOnly = sampleQuestions.filter { it.category == "Design" }
        assertEquals(1, designOnly.size)
        assertEquals("q_2", designOnly[0].id)

        val nonExistent = sampleQuestions.filter { it.category == "Science" }
        assertEquals(0, nonExistent.size)
    }

    @Test
    fun testSearchQueryMatching_byTitleContentOrTags() {
        fun search(query: String) = sampleQuestions.filter { q ->
            q.title.contains(query, ignoreCase = true) ||
                q.content.contains(query, ignoreCase = true) ||
                q.tags.any { it.contains(query, ignoreCase = true) }
        }

        // Search by title keyword
        val resultJee = search("JEE")
        assertEquals(1, resultJee.size)

        // Search by tag keyword
        val resultFigma = search("Figma")
        assertEquals(1, resultFigma.size)
        assertEquals("q_2", resultFigma[0].id)

        // Search by content keyword
        val resultCAT = search("CAT")
        assertEquals(1, resultCAT.size)
        assertEquals("q_3", resultCAT[0].id)

        // Case-insensitive check
        val resultAndroidLower = search("android")
        assertEquals(1, resultAndroidLower.size)
    }

    @Test
    fun testUpvoteCalculationAndVerifiedMentorFlags() {
        val q = sampleQuestions[0]
        val userUpvotedIds = setOf("q_1")

        val isUpvoted = userUpvotedIds.contains(q.id)
        val computedUpvoteCount = q.upvotes + (if (isUpvoted) 1 else 0)

        assertTrue(isUpvoted)
        assertEquals(41, computedUpvoteCount)

        // Verify verified mentor answer presence
        val mentorAnswer = q.answers.firstOrNull { it.isVerifiedMentor }
        assertTrue(mentorAnswer != null)
        assertEquals("IIT Alum", mentorAnswer?.authorBadge)
    }

    @Test
    fun testAddingNewQuestionAndAnswer_immutability() {
        val newQ = CommunityQuestion(
            id = "q_new",
            title = "How to prepare for IISER IAT test?",
            author = "Vikas",
            category = "Science",
            content = "Looking for physics books.",
            timestamp = 1715300000000L
        )

        val updatedList = listOf(newQ) + sampleQuestions
        assertEquals(4, updatedList.size)
        assertEquals("q_new", updatedList[0].id)

        // Add answer to new question
        val answer = CommunityAnswer(
            id = "a_new",
            author = "Dr. Sengupta",
            authorBadge = "TIFR Postdoc",
            isVerifiedMentor = true,
            content = "Use NCERT + HC Verma for Physics.",
            timestamp = 1715310000000L
        )

        val answeredQ = newQ.copy(answers = listOf(answer))
        assertEquals(1, answeredQ.answers.size)
        assertTrue(answeredQ.answers[0].isVerifiedMentor)
    }
}
