package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room database entity storing email metadata and content:
 * - sender
 * - subject
 * - bodySnippet
 * - timestamp
 * - isRead
 * along with rich attributes for AI priority, encryption, labels,
 * summary cards, threading, snooze, nudges, and confidential mode.
 */
@Entity(tableName = "emails")
data class EmailEntity(
    @PrimaryKey
    val id: String,
    val sender: String, // Explicit email metadata: sender name/display
    val senderEmail: String,
    val recipientEmail: String = "me@betamail.io",
    val subject: String, // Explicit email metadata: subject
    val bodySnippet: String, // Explicit email metadata: body snippet
    val body: String,
    val timestamp: Long, // Explicit email metadata: timestamp
    val isRead: Boolean = false, // Explicit email metadata: isRead boolean flag
    val aiSummary: String = "",
    val aiPriorityScore: Int = 50, // 0 - 100
    val aiPriorityReason: String = "",
    val category: String = EmailCategory.PRIMARY.name, // from EmailCategory.name
    val labelsRaw: String = "", // comma-separated strings
    val isStarred: Boolean = false,
    val isArchived: Boolean = false,
    val isDeleted: Boolean = false,
    val isEncrypted: Boolean = false,
    val encryptionCipher: String = "AES-256-GCM",
    val attachmentsRaw: String = "", // formatted name|size|type;name|size|type
    val suggestedRepliesRaw: String = "", // semicolon-separated
    val isDraft: Boolean = false,
    val isQueuedForSync: Boolean = false,

    // Snooze & Follow-up Nudges
    val snoozeUntil: Long = 0L,
    val nudgeText: String = "", // e.g. "Received 3 days ago. Reply?"

    // Scheduled Send & Confidential Mode
    val isScheduled: Boolean = false,
    val scheduledTimestamp: Long = 0L,
    val isConfidential: Boolean = false,
    val confidentialExpiration: String = "",
    val requiresSmsPasscode: Boolean = false,
    val smsPasscode: String = "",

    // Summary Cards & Continuous Threading
    val summaryCardType: String = "", // "PACKAGE", "BILL", "TRAVEL", "EVENT"
    val summaryCardDataRaw: String = "", // key:value;key:value
    val threadMessagesRaw: String = "", // formatted id|sender|email|time|body;;...

    // Spam & Mailing List / Mass Unsubscribe
    val isSpam: Boolean = false,
    val isUnsubscribed: Boolean = false,
    val mailingListSender: String = ""
) {
    // Convenient alias for UI backward compatibility
    val senderName: String get() = sender

    val isSnoozed: Boolean get() = snoozeUntil > System.currentTimeMillis()

    val labels: List<String>
        get() = if (labelsRaw.isBlank()) emptyList() else labelsRaw.split(",").map { it.trim() }.filter { it.isNotEmpty() }

    val suggestedReplies: List<String>
        get() = if (suggestedRepliesRaw.isBlank()) emptyList() else suggestedRepliesRaw.split(";").map { it.trim() }.filter { it.isNotEmpty() }

    val attachments: List<EmailAttachment>
        get() {
            if (attachmentsRaw.isBlank()) return emptyList()
            return attachmentsRaw.split(";").mapNotNull { item ->
                val parts = item.split("|")
                if (parts.size >= 3) {
                    EmailAttachment(
                        name = parts[0],
                        size = parts[1],
                        type = parts[2],
                        isEncrypted = isEncrypted
                    )
                } else null
            }
        }

    val summaryCard: SummaryCard?
        get() {
            if (summaryCardType.isBlank()) return null
            val map = summaryCardDataRaw.split(";").mapNotNull {
                val pair = it.split("=")
                if (pair.size == 2) pair[0] to pair[1] else null
            }.toMap()

            return when (summaryCardType) {
                "PACKAGE" -> SummaryCard.PackageTracking(
                    trackingNumber = map["tracking"] ?: "1Z9999999999999999",
                    carrier = map["carrier"] ?: "UPS Express",
                    status = map["status"] ?: "Out for Delivery",
                    deliveryDate = map["delivery"] ?: "Today by 7:00 PM",
                    itemDescription = map["desc"] ?: "Ultralight Expedition Backpack (Nordic Gear)"
                )
                "BILL" -> SummaryCard.BillPayment(
                    vendor = map["vendor"] ?: "Stripe Cloud Services",
                    amountDue = map["amount"] ?: "$149.00 USD",
                    dueDate = map["dueDate"] ?: "Due Today",
                    invoiceId = map["invoice"] ?: "INV-2026-8819",
                    status = map["status"] ?: "Auto-paid / Verified"
                )
                "TRAVEL" -> SummaryCard.TravelReservation(
                    airline = map["airline"] ?: "SkyWings Aero",
                    flightNumber = map["flight"] ?: "SW-842",
                    departureAirport = map["from"] ?: "JFK (Terminal 2)",
                    arrivalAirport = map["to"] ?: "SFO (Terminal 3)",
                    departureTime = map["depTime"] ?: "Tomorrow, 08:30 AM",
                    arrivalTime = map["arrTime"] ?: "Tomorrow, 11:45 AM",
                    gate = map["gate"] ?: "B14",
                    seat = map["seat"] ?: "4A",
                    boardingGroup = map["group"] ?: "Zone 1 Priority",
                    bookingRef = map["ref"] ?: "BK-9942A"
                )
                "EVENT" -> SummaryCard.EventItinerary(
                    title = map["title"] ?: "Series B Term Sheet Review",
                    dateTime = map["date"] ?: "Tomorrow, 10:30 AM - 11:30 AM PT",
                    location = map["loc"] ?: "Google Meet / Apex Offices",
                    organizer = map["host"] ?: "Marcus Thorne (Apex Capital)",
                    attendeeCount = map["count"]?.toIntOrNull() ?: 5,
                    rsvpStatus = map["rsvp"] ?: "Accepted"
                )
                else -> null
            }
        }

    val threadMessages: List<ThreadMessage>
        get() {
            if (threadMessagesRaw.isBlank()) {
                return listOf(
                    ThreadMessage(
                        id = id,
                        senderName = sender,
                        senderEmail = senderEmail,
                        timestamp = timestamp,
                        body = body,
                        attachmentsRaw = attachmentsRaw
                    )
                )
            }
            return threadMessagesRaw.split(";;").mapNotNull { msgStr ->
                val p = msgStr.split("||")
                if (p.size >= 5) {
                    ThreadMessage(
                        id = p[0],
                        senderName = p[1],
                        senderEmail = p[2],
                        timestamp = p[3].toLongOrNull() ?: timestamp,
                        body = p[4],
                        attachmentsRaw = if (p.size > 5) p[5] else ""
                    )
                } else null
            }
        }
}
