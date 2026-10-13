package com.adityayadav.careerguidancecommunity

import com.adityayadav.careerguidancecommunity.data.model.ApplicationStage
import com.adityayadav.careerguidancecommunity.data.model.TrackedScholarship
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ApplicationTrackerFunctionalTest {

    @Test
    fun testStageTransitions_followLifecycle() {
        var application = TrackedScholarship(
            scholarshipId = "sch_reliance_01",
            scholarshipName = "Reliance Foundation Undergraduate Scholarship",
            provider = "Reliance Foundation",
            amount = "₹2,00,000",
            deadline = "October 31",
            stage = ApplicationStage.DISCOVERED
        )

        assertEquals(ApplicationStage.DISCOVERED, application.stage)

        // Advance to Docs Ready
        application = application.copy(stage = ApplicationStage.DOCS_READY)
        assertEquals(ApplicationStage.DOCS_READY, application.stage)

        // Advance to Applied
        application = application.copy(stage = ApplicationStage.APPLIED)
        assertEquals(ApplicationStage.APPLIED, application.stage)

        // Advance to Awarded
        application = application.copy(stage = ApplicationStage.AWARDED)
        assertEquals(ApplicationStage.AWARDED, application.stage)
    }

    @Test
    fun testDocumentChecklist_progressCalculation() {
        val standardDocs = listOf(
            "Academic Mark Sheets & Transcripts",
            "Family Income / EWS Certificate",
            "Government Photo ID (Aadhaar / Voter ID)",
            "Statement of Purpose (SOP)",
            "Letter of Recommendation (LOR)"
        )

        var item = TrackedScholarship(
            scholarshipId = "sch_aicte_pragati",
            scholarshipName = "AICTE Pragati Scholarship",
            provider = "AICTE",
            amount = "₹50,000 / year",
            deadline = "November 15"
        )

        assertEquals(0, item.completedDocuments.size)

        // Add 2 documents
        val docsAfter2 = item.completedDocuments.toMutableSet().apply {
            add("Academic Mark Sheets & Transcripts")
            add("Family Income / EWS Certificate")
        }
        item = item.copy(completedDocuments = docsAfter2)

        val completionFraction = item.completedDocuments.size.toFloat() / standardDocs.size.toFloat()
        assertEquals(0.4f, completionFraction, 0.001f)

        // Toggle / Remove one document
        val docsAfterRemove = item.completedDocuments.toMutableSet().apply {
            remove("Family Income / EWS Certificate")
        }
        item = item.copy(completedDocuments = docsAfterRemove)
        assertEquals(1, item.completedDocuments.size)
        assertFalse(item.completedDocuments.contains("Family Income / EWS Certificate"))
        assertTrue(item.completedDocuments.contains("Academic Mark Sheets & Transcripts"))
    }

    @Test
    fun testNotesAndMetadataUpdates() {
        val initialTimestamp = System.currentTimeMillis()
        var item = TrackedScholarship(
            scholarshipId = "sch_dst_inspire",
            scholarshipName = "DST INSPIRE Fellowship",
            provider = "DST",
            amount = "₹80,000 / year",
            deadline = "December 31",
            notes = "",
            updatedAt = initialTimestamp
        )

        val newNotes = "Reference ID: INSPIRE-2026-90412. Interview scheduled for Nov 20."
        val updatedTimestamp = initialTimestamp + 5000L

        item = item.copy(notes = newNotes, updatedAt = updatedTimestamp)

        assertEquals(newNotes, item.notes)
        assertTrue(item.updatedAt > initialTimestamp)
    }
}
