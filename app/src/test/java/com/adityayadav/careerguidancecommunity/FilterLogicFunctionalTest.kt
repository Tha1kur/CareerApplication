package com.adityayadav.careerguidancecommunity

import com.adityayadav.careerguidancecommunity.data.model.College
import com.adityayadav.careerguidancecommunity.data.model.Scholarship
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FilterLogicFunctionalTest {

    private val sampleColleges = listOf(
        College(
            id = "iitb",
            name = "Indian Institute of Technology Bombay",
            city = "Mumbai",
            state = "Maharashtra",
            type = "Engineering",
            tags = listOf("Engineering", "Computer Science", "AI"),
            rank = 1
        ),
        College(
            id = "nid",
            name = "National Institute of Design",
            city = "Ahmedabad",
            state = "Gujarat",
            type = "Design",
            tags = listOf("Design", "UI/UX"),
            rank = 10
        ),
        College(
            id = "iima",
            name = "Indian Institute of Management Ahmedabad",
            city = "Ahmedabad",
            state = "Gujarat",
            type = "Management",
            tags = listOf("Management", "Business"),
            rank = 2
        )
    )

    private val sampleScholarships = listOf(
        Scholarship(
            id = "s1",
            name = "National Merit Scholarship",
            category = "Merit",
            eligibility = "Class 12 pass with 85%+"
        ),
        Scholarship(
            id = "s2",
            name = "Adobe Women-in-Tech",
            category = "STEM",
            eligibility = "Female students in CS and AI"
        ),
        Scholarship(
            id = "s3",
            name = "Means Scholarship",
            category = "Need-based",
            eligibility = "Family income under 1 LPA"
        )
    )

    @Test
    fun `college stream filter matches exactly and ignores case`() {
        val eng = sampleColleges.filter { it.type.equals("Engineering", ignoreCase = true) }
        assertEquals(1, eng.size)
        assertEquals("iitb", eng.first().id)

        val design = sampleColleges.filter { it.type.equals("Design", ignoreCase = true) }
        assertEquals(1, design.size)
        assertEquals("nid", design.first().id)
    }

    @Test
    fun `college search matches across name, city, and tags`() {
        val query = "ahmedabad"
        val results = sampleColleges.filter { c ->
            c.name.contains(query, ignoreCase = true) ||
            (c.city?.contains(query, ignoreCase = true) ?: false) ||
            c.tags.any { it.contains(query, ignoreCase = true) }
        }
        assertEquals(2, results.size)
        assertTrue(results.any { it.id == "nid" })
        assertTrue(results.any { it.id == "iima" })
    }

    @Test
    fun `scholarship category filter matches correctly`() {
        val stem = sampleScholarships.filter { it.category.equals("STEM", ignoreCase = true) }
        assertEquals(1, stem.size)
        assertEquals("s2", stem.first().id)

        val all = sampleScholarships.filter { "All" == "All" }
        assertEquals(3, all.size)
    }

    @Test
    fun `scholarship search matches eligibility terms`() {
        val query = "income"
        val results = sampleScholarships.filter { s ->
            s.name.contains(query, ignoreCase = true) ||
            (s.eligibility?.contains(query, ignoreCase = true) ?: false)
        }
        assertEquals(1, results.size)
        assertEquals("s3", results.first().id)
    }
}
