package com.adityayadav.careerguidancecommunity

import com.adityayadav.careerguidancecommunity.data.AiCareerMentorEngine
import com.adityayadav.careerguidancecommunity.data.model.Profile
import com.adityayadav.careerguidancecommunity.data.model.UserProgress
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AiCareerMentorFunctionalTest {

    @Test
    fun testEngineeringPlanGeneration_matchesHighScores() {
        val scores = mapOf(
            "Engineering" to 32,
            "Design" to 10,
            "Management" to 8,
            "Science" to 14,
            "Entrepreneurship" to 6
        )
        val profile = Profile(name = "Aman Gupta", age = 18, goal = "Build software")
        val progress = UserProgress(points = 200)

        val plan = AiCareerMentorEngine.generatePlan(profile, scores, progress)

        assertEquals("Engineering", plan.careerCategory)
        assertEquals("Software & Systems Innovator", plan.careerTitle)
        assertTrue(plan.matchPercentage >= 75)
        assertTrue(plan.keyStrengths.isNotEmpty())
        assertTrue(plan.targetEntranceExams.contains("JEE Advanced"))
        assertTrue(plan.milestones.isNotEmpty())
        assertTrue(plan.milestones.any { it.priority == "Essential" })
    }

    @Test
    fun testDesignPlanGeneration_matchesCreativeProfile() {
        val scores = mapOf(
            "Engineering" to 8,
            "Design" to 30,
            "Management" to 12,
            "Science" to 6,
            "Entrepreneurship" to 10
        )
        val profile = Profile(name = "Ananya Roy", age = 19, goal = "Become a UX Designer")
        val progress = UserProgress(points = 150)

        val plan = AiCareerMentorEngine.generatePlan(profile, scores, progress)

        assertEquals("Design", plan.careerCategory)
        assertEquals("Product Designer & UX Strategist", plan.careerTitle)
        assertTrue(plan.targetEntranceExams.contains("UCEED"))
        assertTrue(plan.recommendedSkills.any { it.contains("Figma") })
        assertTrue(plan.milestones.any { it.id == "des_1" })
    }

    @Test
    fun testManagementAndSciencePlanGeneration() {
        val mgmtScores = mapOf("Management" to 28, "Engineering" to 10)
        val mgmtPlan = AiCareerMentorEngine.generatePlan(Profile(name = "Rohan"), mgmtScores, UserProgress())
        assertEquals("Management", mgmtPlan.careerCategory)
        assertTrue(mgmtPlan.targetEntranceExams.contains("CAT"))

        val sciScores = mapOf("Science" to 29, "Engineering" to 12)
        val sciPlan = AiCareerMentorEngine.generatePlan(Profile(name = "Divya"), sciScores, UserProgress())
        assertEquals("Science", sciPlan.careerCategory)
        assertTrue(sciPlan.targetEntranceExams.contains("NEST (NISER/CEBS)"))
    }

    @Test
    fun testFallbackInferenceFromGoal_whenScoresEmpty() {
        val emptyScores = emptyMap<String, Int>()
        val profile = Profile(name = "Sameer", age = 20, goal = "I want to launch a startup and build a business")
        val plan = AiCareerMentorEngine.generatePlan(profile, emptyScores, UserProgress())

        assertEquals("Entrepreneurship", plan.careerCategory)
        assertEquals("Venture Founder & Innovation Catalyst", plan.careerTitle)
        assertTrue(plan.milestones.any { it.title.contains("MVP") })
    }

    @Test
    fun testMilestoneActionRoutes_areValid() {
        val scores = mapOf("Engineering" to 25)
        val plan = AiCareerMentorEngine.generatePlan(Profile(name = "Dev"), scores, UserProgress())

        val validRoutes = setOf("quiz", "colleges", "scholarships", "compare", "community")
        plan.milestones.forEach { milestone ->
            assertNotNull(milestone.id)
            if (milestone.actionRoute != null) {
                assertTrue("Invalid route: ${milestone.actionRoute}", milestone.actionRoute in validRoutes)
            }
        }
    }
}
