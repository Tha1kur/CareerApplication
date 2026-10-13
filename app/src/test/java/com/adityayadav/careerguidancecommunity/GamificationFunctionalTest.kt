package com.adityayadav.careerguidancecommunity

import com.adityayadav.careerguidancecommunity.data.model.calculateLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GamificationFunctionalTest {

    @Test
    fun `zero points initializes at level 1 with zero progress`() {
        val info = calculateLevel(0)
        assertEquals(1, info.level)
        assertEquals(0, info.currentXp)
        assertEquals(150, info.nextLevelXp)
        assertEquals(0f, info.progressFraction, 0.001f)
    }

    @Test
    fun `halfway progress calculates correct fraction`() {
        val info = calculateLevel(75)
        assertEquals(1, info.level)
        assertEquals(75, info.currentXp)
        assertEquals(0.5f, info.progressFraction, 0.001f)
    }

    @Test
    fun `exact level boundary promotes to level 2 with zero excess`() {
        val info = calculateLevel(150)
        assertEquals(2, info.level)
        assertEquals(0, info.currentXp)
        assertEquals(0f, info.progressFraction, 0.001f)
    }

    @Test
    fun `high XP advances to level 7 with fractional remainder`() {
        val info = calculateLevel(950)
        // 950 / 150 = 6 remainder 50 -> level 7
        assertEquals(7, info.level)
        assertEquals(50, info.currentXp)
        assertEquals(50f / 150f, info.progressFraction, 0.001f)
        assertTrue(info.progressFraction in 0f..1f)
    }
}
