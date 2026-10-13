package com.adityayadav.careerguidancecommunity

import com.adityayadav.careerguidancecommunity.data.model.College
import com.adityayadav.careerguidancecommunity.data.model.QuizQuestion
import com.adityayadav.careerguidancecommunity.data.model.Scholarship
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class JsonAssetSerializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun `aptitudequiz json decodes into non-empty questions list with options`() {
        val file = File("src/main/assets/aptitudequiz.json")
        assertTrue("Asset file must exist at ${file.absolutePath}", file.exists())

        val raw = file.readText()
        val questions = json.decodeFromString<List<QuizQuestion>>(raw)

        assertFalse(questions.isEmpty())
        assertEquals(8, questions.size)

        questions.forEach { q ->
            assertNotNull(q.id)
            assertNotNull(q.question)
            assertFalse(q.options.isEmpty())
            q.options.forEach { opt ->
                assertNotNull(opt.value)
                assertNotNull(opt.label)
            }
        }
    }

    @Test
    fun `colleges json decodes cleanly with intake as string and non-null ids`() {
        val file = File("src/main/assets/colleges.json")
        assertTrue("Asset file must exist at ${file.absolutePath}", file.exists())

        val raw = file.readText()
        val colleges = json.decodeFromString<List<College>>(raw)

        assertFalse(colleges.isEmpty())
        colleges.forEach { c ->
            assertNotNull(c.id)
            assertNotNull(c.name)
            assertNotNull(c.type)
            // Verify intake string doesn't cause any runtime crashes
            assertTrue(c.intake == null || c.intake.isNotBlank())
        }
    }

    @Test
    fun `scholarships json decodes cleanly into non-empty scholarship list`() {
        val file = File("src/main/assets/scholarships.json")
        assertTrue("Asset file must exist at ${file.absolutePath}", file.exists())

        val raw = file.readText()
        val scholarships = json.decodeFromString<List<Scholarship>>(raw)

        assertFalse(scholarships.isEmpty())
        scholarships.forEach { s ->
            assertNotNull(s.id)
            assertNotNull(s.name)
            assertNotNull(s.category)
        }
    }
}
