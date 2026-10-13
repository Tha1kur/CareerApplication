package com.adityayadav.careerguidancecommunity

import com.adityayadav.careerguidancecommunity.data.model.College
import com.adityayadav.careerguidancecommunity.data.model.Scholarship
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BookmarkLogicFunctionalTest {

    @Test
    fun `bookmark set toggling adds and removes IDs accurately`() {
        var savedIds = emptySet<String>()

        fun toggle(id: String) {
            savedIds = if (savedIds.contains(id)) savedIds - id else savedIds + id
        }

        toggle("college_1")
        assertTrue(savedIds.contains("college_1"))
        assertEquals(1, savedIds.size)

        toggle("college_2")
        assertTrue(savedIds.contains("college_2"))
        assertEquals(2, savedIds.size)

        // Toggling college_1 again removes it
        toggle("college_1")
        assertFalse(savedIds.contains("college_1"))
        assertTrue(savedIds.contains("college_2"))
        assertEquals(1, savedIds.size)
    }

    @Test
    fun `filtering items by bookmarked IDs returns matched subset`() {
        val allColleges = listOf(
            College(id = "c1", name = "IIT Bombay"),
            College(id = "c2", name = "IIT Delhi"),
            College(id = "c3", name = "BITS Pilani")
        )
        val bookmarkedIds = setOf("c1", "c3")

        val bookmarkedColleges = allColleges.filter { it.id in bookmarkedIds }

        assertEquals(2, bookmarkedColleges.size)
        assertEquals("IIT Bombay", bookmarkedColleges[0].name)
        assertEquals("BITS Pilani", bookmarkedColleges[1].name)
    }

    @Test
    fun `scholarship bookmarking preserves scholarship entities`() {
        val scholarships = listOf(
            Scholarship(id = "s1", name = "KVPY", category = "Science"),
            Scholarship(id = "s2", name = "National Talent", category = "Merit"),
            Scholarship(id = "s3", name = "Inspire", category = "Science")
        )
        val saved = setOf("s2")

        val filtered = scholarships.filter { it.id in saved }

        assertEquals(1, filtered.size)
        assertEquals("National Talent", filtered[0].name)
    }
}
