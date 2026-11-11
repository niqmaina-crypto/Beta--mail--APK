package com.example.ai

data class BetaAIOpinion(
    val tone: String,
    val clarityScore: Int,
    val summaryOpinion: String,
    val recommendation: String,
    val readingTimeSeconds: Int
)

data class BetaAISuggestion(
    val id: String,
    val label: String,
    val content: String,
    val category: String // "Closing", "Action Item", "Clarification", "Urgency"
)

data class DocumentSummary(
    val fileName: String,
    val fileType: String,
    val keyPoints: List<String>,
    val actionItems: List<String>,
    val confidenceScore: String = "Gemini 1.5 Pro • 99.4% Verified"
)

object BetaAIWritingCoach {

    fun analyzeEmail(subject: String, body: String): BetaAIOpinion {
        val wordCount = body.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }.size
        val readingTime = maxOf(3, (wordCount * 0.3).toInt())

        val isUrgent = subject.contains("urgent", ignoreCase = true) ||
                body.contains("deadline", ignoreCase = true) ||
                body.contains("asap", ignoreCase = true) ||
                body.contains("eod", ignoreCase = true)

        val hasGreeting = body.startsWith("hi", ignoreCase = true) ||
                body.startsWith("dear", ignoreCase = true) ||
                body.startsWith("hello", ignoreCase = true)

        val hasSignoff = body.contains("regards", ignoreCase = true) ||
                body.contains("thanks", ignoreCase = true) ||
                body.contains("best", ignoreCase = true) ||
                body.contains("sincerely", ignoreCase = true)

        val tone = when {
            body.isBlank() -> "Awaiting Draft"
            isUrgent -> "Direct & High Urgency"
            body.contains("pleased", ignoreCase = true) || body.contains("excited", ignoreCase = true) -> "Enthusiastic & Collaborative"
            hasGreeting && hasSignoff -> "Polished & Professional"
            else -> "Direct & Casual"
        }

        val clarityScore = when {
            body.isBlank() -> 50
            wordCount < 10 -> 65
            wordCount in 20..150 -> 94
            wordCount in 151..300 -> 86
            else -> 74
        }

        val opinion = when {
            body.isBlank() -> "Start typing your message above. Gemini in Betamail will evaluate tone, readability, and offer live contextual Smart Compose suggestions."
            wordCount < 15 -> "Opinion: The draft is very brief. Adding a concrete reason or timeline will make your request much clearer to the recipient."
            isUrgent && !body.contains("PM", ignoreCase = true) && !body.contains("AM", ignoreCase = true) -> "Opinion: High urgency is expressed, but specifying an exact hour or timezone for the deadline will prevent misunderstandings."
            !hasSignoff -> "Opinion: The core message is solid, but adding a courteous closing phrase will maintain warm stakeholder relations."
            else -> "Opinion: Well-balanced email with concise phrasing and professional demeanor. Ready to dispatch."
        }

        val recommendation = when {
            !hasGreeting && body.isNotBlank() -> "Add a personalized salutation (e.g., 'Hi [Name],')."
            isUrgent -> "Include explicit call-to-action steps and confirmation request."
            wordCount > 180 -> "Consider breaking long paragraphs into bullet points for faster mobile scanning."
            else -> "Review Smart Compose suggestions below. Press Tab or tap [Tab ⇥] to accept."
        }

        return BetaAIOpinion(
            tone = tone,
            clarityScore = clarityScore,
            summaryOpinion = opinion,
            recommendation = recommendation,
            readingTimeSeconds = readingTime
        )
    }

    /**
     * Smart Compose: Predicts inline completion text based on the last words typed.
     */
    fun predictNextWords(currentText: String): String {
        val trimmed = currentText.trimEnd()
        return when {
            trimmed.endsWith("I look forward to", ignoreCase = true) -> " hearing your feedback on the proposal."
            trimmed.endsWith("Please let me know if", ignoreCase = true) -> " you have any questions or need additional clarification."
            trimmed.endsWith("Thank you for", ignoreCase = true) -> " your time and collaboration on this project."
            trimmed.endsWith("Could you please", ignoreCase = true) -> " confirm receipt of the attached documents by EOD?"
            trimmed.endsWith("Let's schedule a", ignoreCase = true) -> " quick 15-minute call tomorrow to align."
            trimmed.endsWith("Best", ignoreCase = true) -> " regards,\nAlex Mercer"
            trimmed.endsWith("Hi", ignoreCase = true) -> " Team,\n\nHope everyone is having a great week."
            trimmed.isEmpty() -> "I am writing to share an update regarding our recent milestones..."
            else -> " please let me know if you need any additional details."
        }
    }

    fun polishDraft(currentText: String, style: String): String {
        return when (style) {
            "Formal" -> {
                "Dear Colleague,\n\nI trust this message finds you well. I am writing to formally present our status report and request your guidance on the matter at hand. $currentText\n\nSincerely,\nAlex Mercer"
            }
            "Shorten" -> {
                "Update: $currentText\n\nAction needed by EOD. Please confirm."
            }
            "Friendly" -> {
                "Hi there! 😊\n\nHope you're having a wonderful week! Wanted to quickly check in regarding this: $currentText\n\nCheers,\nAlex"
            }
            "Elaborate" -> {
                "$currentText\n\nKey Background & Context:\n• Technical and security audits have completed successfully.\n• Full compliance documentation is ready for stakeholder distribution.\n• All milestones remain on schedule for European regulatory filing."
            }
            else -> currentText
        }
    }

    fun summarizeDocument(fileName: String): DocumentSummary {
        return when {
            fileName.contains("Audit", ignoreCase = true) -> DocumentSummary(
                fileName = fileName,
                fileType = "PDF Security Audit",
                keyPoints = listOf(
                    "Comprehensive Q4 Cryptographic Audit passed 100% of zero-knowledge tests.",
                    "Section 4.2 establishes deterministic key derivation with zero server-side exposure.",
                    "Meets all European GDPR and NIST post-quantum compliance standards."
                ),
                actionItems = listOf(
                    "Executive sign-off required before 5:00 PM EST today.",
                    "Verify public fingerprint 0x4F8B against HSM hardware token."
                )
            )
            fileName.contains("Term_Sheet", ignoreCase = true) -> DocumentSummary(
                fileName = fileName,
                fileType = "PDF Investment Agreement",
                keyPoints = listOf(
                    "Apex Capital leads Series B with $42M pre-money valuation.",
                    "Pro-rata rights and 1 Board of Directors seat allocated to Lead Partner.",
                    "Standard 1x non-participating liquidation preference."
                ),
                actionItems = listOf(
                    "Review governance clauses with outside legal counsel.",
                    "Attend scheduled sync tomorrow at 10:30 AM PT."
                )
            )
            fileName.contains("Invoice", ignoreCase = true) -> DocumentSummary(
                fileName = fileName,
                fileType = "Tax Invoice",
                keyPoints = listOf(
                    "Invoice total: $149.00 USD for 3x Dedicated Cloud Nodes.",
                    "Billing cycle: Nov 1 - Nov 30, 2026. Paid via card ending in 4022.",
                    "Zero outstanding balance."
                ),
                actionItems = listOf(
                    "Archive for quarterly corporate tax reconciliation."
                )
            )
            else -> DocumentSummary(
                fileName = fileName,
                fileType = "Attached Document",
                keyPoints = listOf(
                    "Gemini scanned attachment contents: Document verified clean.",
                    "Structured summary generated automatically without external data leaks."
                ),
                actionItems = listOf(
                    "Review attached file in secure sandboxed viewer."
                )
            )
        }
    }

    fun getSuggestionsForContext(subject: String, body: String): List<BetaAISuggestion> {
        val suggestions = mutableListOf<BetaAISuggestion>()

        if (body.isBlank()) {
            suggestions.add(
                BetaAISuggestion(
                    id = "sug_open_formal",
                    label = "Formal Greeting",
                    content = "Dear Colleague,\n\nI hope this email finds you well. I am writing to provide a key update regarding our project milestones.",
                    category = "Opening"
                )
            )
            suggestions.add(
                BetaAISuggestion(
                    id = "sug_open_direct",
                    label = "Quick Check-in",
                    content = "Hi team,\n\nReaching out quickly to align on our deliverables and ensure we have everything in place for upcoming reviews.",
                    category = "Opening"
                )
            )
        } else {
            if (!body.contains("deadline", ignoreCase = true) && !body.contains("EOD", ignoreCase = true)) {
                suggestions.add(
                    BetaAISuggestion(
                        id = "sug_action_deadline",
                        label = "Add Action Timeline",
                        content = "\n\nCould you kindly review and confirm by 5:00 PM EST today so we can finalize the schedule?",
                        category = "Action Item"
                    )
                )
            }

            if (!body.contains("call", ignoreCase = true) && !body.contains("meeting", ignoreCase = true)) {
                suggestions.add(
                    BetaAISuggestion(
                        id = "sug_schedule_sync",
                        label = "Propose Short Sync",
                        content = "\n\nIf you prefer to talk through this live, let me know your availability for a 15-minute sync tomorrow.",
                        category = "Clarification"
                    )
                )
            }

            suggestions.add(
                BetaAISuggestion(
                    id = "sug_supportive_closing",
                    label = "Polite Closing",
                    content = "\n\nThank you for your guidance and support on this. Please let me know if you have any questions.\n\nBest regards,\n",
                    category = "Closing"
                )
            )

            suggestions.add(
                BetaAISuggestion(
                    id = "sug_attachments_ref",
                    label = "Reference Encrypted Docs",
                    content = "\n\nI have securely attached the verified cryptographic audit bundle for your reference.",
                    category = "Urgency"
                )
            )
        }

        return suggestions
    }
}
