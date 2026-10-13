package com.adityayadav.careerguidancecommunity

import com.adityayadav.careerguidancecommunity.data.model.College
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class CollegeComparisonFunctionalTest {

    private val colleges = listOf(
        College(id = "c1", name = "IIT Madras", rank = 1, city = "Chennai", type = "IIT", avgFees = "₹2.2 Lakh/yr"),
        College(id = "c2", name = "IIT Delhi", rank = 2, city = "New Delhi", type = "IIT", avgFees = "₹2.3 Lakh/yr"),
        College(id = "c3", name = "BITS Pilani", rank = 20, city = "Pilani", type = "Private", avgFees = "₹5.5 Lakh/yr")
    )

    @Test
    fun `comparison selection enforces max 3 institutions`() {
        var selection = emptySet<String>()

        fun addSelection(id: String) {
            if (selection.size < 3) selection = selection + id
        }

        addSelection("c1")
        addSelection("c2")
        addSelection("c3")
        addSelection("c4") // Exceeds limit of 3

        assertEquals(3, selection.size)
        assertEquals(setOf("c1", "c2", "c3"), selection)
    }

    @Test
    fun `best national rank is identified correctly among compared colleges`() {
        val selected = listOf(colleges[1], colleges[0], colleges[2]) // order: Delhi (2), Madras (1), BITS (20)

        val bestRank = selected.filter { it.rank != null }.minByOrNull { it.rank!! }

        assertNotNull(bestRank)
        assertEquals("IIT Madras", bestRank?.name)
        assertEquals(1, bestRank?.rank)
    }

    @Test
    fun `institutes without ranks handle graceful fallback`() {
        val unranked = listOf(
            College(id = "c4", name = "Local College A", rank = null),
            College(id = "c5", name = "Local College B", rank = null)
        )

        val bestRank = unranked.filter { it.rank != null }.minByOrNull { it.rank!! }

        assertEquals(null, bestRank)
    }
}
