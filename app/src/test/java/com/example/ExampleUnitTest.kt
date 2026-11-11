package com.example

import com.example.ai.BetaAIWritingCoach
import com.example.data.local.AppDatabase
import com.example.data.model.EmailCategory
import com.example.data.model.EmailEntity
import com.example.data.model.NotificationRule
import com.example.data.model.UserProfileEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun sampleEmailsAreLoadedAndScored() {
        val sampleEmails = AppDatabase.getInitialSampleEmails()
        assertTrue("Sample emails should not be empty", sampleEmails.isNotEmpty())

        val highPriority = sampleEmails.find { it.aiPriorityScore >= 90 }
        assertNotNull("Should contain at least one high priority email", highPriority)
        assertTrue(highPriority!!.isStarred || highPriority.isEncrypted)
        assertTrue(highPriority.labels.contains("Urgent") || highPriority.labels.contains("Action Needed"))
    }

    @Test
    fun emailMetadataEntityFieldsValidation() {
        // Testing required metadata: sender, subject, body snippet, timestamp, isRead
        val email = EmailEntity(
            id = "msg_metadata_test",
            sender = "Marcus Thorne",
            senderEmail = "marcus@apex.io",
            subject = "Contract Review & Signoff",
            bodySnippet = "Please find attached the signed contract...",
            body = "Please find attached the signed contract for our partnership.",
            timestamp = 1700000000000L,
            isRead = false,
            aiSummary = "Contract review requested",
            aiPriorityScore = 88,
            aiPriorityReason = "Signature required",
            category = EmailCategory.PRIMARY.name,
            labelsRaw = "Urgent, Action Needed, Work",
            attachmentsRaw = "Contract.pdf|1.2 MB|pdf",
            suggestedRepliesRaw = "Signed and sent;Need more time"
        )

        // Explicit metadata verification
        assertEquals("Marcus Thorne", email.sender)
        assertEquals("Contract Review & Signoff", email.subject)
        assertEquals("Please find attached the signed contract...", email.bodySnippet)
        assertEquals(1700000000000L, email.timestamp)
        assertFalse(email.isRead)

        assertEquals(3, email.labels.size)
        assertTrue(email.labels.contains("Urgent"))
        assertEquals(1, email.attachments.size)
        assertEquals(2, email.suggestedReplies.size)
    }

    @Test
    fun userProfileEntityFieldsValidation() {
        val profile = UserProfileEntity(
            id = 1,
            emailAddress = "alex.mercer@betamail.io",
            displayName = "Alex Mercer",
            passwordHash = "SecuredPassword#2026",
            profilePicture = "avatar_cyber_executive",
            isConfigured = true
        )

        assertEquals("alex.mercer@betamail.io", profile.emailAddress)
        assertEquals("Alex Mercer", profile.displayName)
        assertEquals("SecuredPassword#2026", profile.passwordHash)
        assertEquals("avatar_cyber_executive", profile.profilePicture)
        assertTrue(profile.isConfigured)
    }

    @Test
    fun betaAIWritingCoachOpinionsAndSuggestions() {
        val draftSubject = "URGENT: Project Beta Launch"
        val draftBody = "Hi team, we need to finalize the deployment today. Please check the code."

        val opinion = BetaAIWritingCoach.analyzeEmail(draftSubject, draftBody)
        assertNotNull(opinion)
        assertTrue(opinion.clarityScore > 0)
        assertTrue(opinion.summaryOpinion.contains("Opinion:"))

        val suggestions = BetaAIWritingCoach.getSuggestionsForContext(draftSubject, draftBody)
        assertTrue("Suggestions should not be empty", suggestions.isNotEmpty())
        assertTrue(suggestions.any { it.category == "Action Item" || it.category == "Closing" })
    }

    @Test
    fun notificationFilterLogicVerification() {
        val urgentEmail = EmailEntity(
            id = "test_urgent",
            sender = "Elena",
            senderEmail = "elena@apexsystems.com",
            subject = "Urgent Security Vulnerability",
            bodySnippet = "Patch needed immediately...",
            body = "Patch needed",
            aiSummary = "Urgent patch needed",
            aiPriorityScore = 95,
            aiPriorityReason = "Critical vulnerability",
            category = EmailCategory.PRIMARY.name,
            labelsRaw = "Urgent",
            timestamp = 1700000000L,
            isRead = false
        )

        val promoEmail = EmailEntity(
            id = "test_promo",
            sender = "Sales",
            senderEmail = "promo@market.io",
            subject = "Black Friday Deal",
            bodySnippet = "50% off backpacks discount...",
            body = "Discount inside",
            aiSummary = "Promotional offer",
            aiPriorityScore = 15,
            aiPriorityReason = "Advertisement",
            category = EmailCategory.PROMOTIONS.name,
            labelsRaw = "Promotions",
            timestamp = 1700000000L,
            isRead = true
        )

        val strictRule = NotificationRule(
            minimumPriorityScore = 75,
            notifyOnlyVips = false
        )

        assertTrue(urgentEmail.aiPriorityScore >= strictRule.minimumPriorityScore)
        assertFalse(promoEmail.aiPriorityScore >= strictRule.minimumPriorityScore)
    }
}
