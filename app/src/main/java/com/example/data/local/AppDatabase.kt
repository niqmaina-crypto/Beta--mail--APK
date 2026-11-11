package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.EmailCategory
import com.example.data.model.EmailEntity
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [EmailEntity::class, UserProfileEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun emailDao(): EmailDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "betamail_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getInstance(context)
                                database.emailDao().insertEmails(getInitialSampleEmails())
                                database.userProfileDao().saveUserProfile(
                                    UserProfileEntity(
                                        id = 1,
                                        emailAddress = "alex.mercer@betamail.io",
                                        displayName = "Alex Mercer",
                                        passwordHash = "BetaMail#2026!Secured",
                                        profilePicture = "avatar_cyber_executive",
                                        createdAt = System.currentTimeMillis(),
                                        isConfigured = true
                                    )
                                )
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun getInitialSampleEmails(): List<EmailEntity> {
            val now = System.currentTimeMillis()
            val hour = 3600 * 1000L
            val day = 24 * hour

            return listOf(
                // 1. Critical audit sign-off with Threading & Follow-up Nudge
                EmailEntity(
                    id = "msg_1",
                    sender = "Elena Vance",
                    senderEmail = "elena.vance@blackmesa.tech",
                    recipientEmail = "alex.mercer@betamail.io",
                    subject = "URGENT: Q4 AI Security Audit & Compliance Sign-off Needed",
                    bodySnippet = "I've finalized our comprehensive Q4 End-to-End Cryptographic Security Audit report. All internal penetration tests have successfully cleared without regressions...",
                    body = """Hi Alex,

I've finalized our comprehensive Q4 End-to-End Cryptographic Security Audit report. All internal penetration tests have successfully cleared without regressions. 

However, we need your executive sign-off on Section 4.2 (Zero-Trust Key Derivation) before 5:00 PM EST today to meet the European regulatory compliance deadline.

The full encrypted documentation and certificate bundle are attached. Please review and reply with your confirmation.

Best regards,
Elena Vance
Chief Information Security Officer""",
                    timestamp = now - (25 * 60 * 1000L),
                    isRead = false,
                    aiSummary = "Executive sign-off required for Section 4.2 of Q4 Cryptographic Security Audit before 5:00 PM EST today for regulatory compliance.",
                    aiPriorityScore = 96,
                    aiPriorityReason = "Requires immediate executive approval with tight deadline today (5:00 PM). High legal/security impact.",
                    category = EmailCategory.PRIMARY.name,
                    labelsRaw = "Action Needed, Urgent, Work, Encrypted",
                    isStarred = true,
                    isArchived = false,
                    isDeleted = false,
                    isEncrypted = true,
                    encryptionCipher = "AES-256-GCM + X25519",
                    attachmentsRaw = "Q4_Audit_Report.pdf|2.4 MB|pdf;Key_Derivation_Spec.enc|480 KB|enc",
                    suggestedRepliesRaw = "Approved, signed and confirmed;Reviewing now, will send comments by 3 PM;Let's jump on a quick 5-min call",
                    nudgeText = "Received 3 hours ago. Reply requested by 5 PM.",
                    threadMessagesRaw = "msg_1_a||Elena Vance||elena.vance@blackmesa.tech||${now - 2 * hour}||Preliminary audit completed. Will send executive bundle shortly.;;msg_1_b||Alex Mercer||alex.mercer@betamail.io||${now - hour}||Standing by. Legal team is ready to verify.;;msg_1||Elena Vance||elena.vance@blackmesa.tech||${now - (25 * 60 * 1000L)}||Full encrypted bundle ready. Need final signature on section 4.2."
                ),

                // 2. Event Itinerary Summary Card: Series B Term Sheet
                EmailEntity(
                    id = "msg_2",
                    sender = "Apex Capital Partners",
                    senderEmail = "partners@apexvc.com",
                    recipientEmail = "alex.mercer@betamail.io",
                    subject = "Series B Term Sheet Review & Investment Committee Meeting",
                    bodySnippet = "Following our partner meeting on Monday, our investment committee has voted to lead your Series B funding round at $42M pre-money...",
                    body = """Dear Founders,

Following our partner meeting on Monday, our investment committee has unanimously voted to lead your Series B funding round at the agreed $42M pre-money valuation.

We have scheduled our final review meeting for tomorrow morning at 10:30 AM PT via Google Meet. Please find the calendar invite and draft term sheet details below.

Looking forward to partnering together.

Marcus Thorne
Managing Partner, Apex Capital""",
                    timestamp = now - (1 * hour),
                    isRead = false,
                    aiSummary = "Apex Capital partners unanimously approved leading Series B funding at $42M pre-money; scheduled meeting tomorrow at 10:30 AM PT.",
                    aiPriorityScore = 91,
                    aiPriorityReason = "Major business milestone: term sheet offer needing meeting confirmation within 24 hours.",
                    category = EmailCategory.PRIMARY.name,
                    labelsRaw = "Action Needed, Finance, Work",
                    isStarred = true,
                    isArchived = false,
                    isDeleted = false,
                    isEncrypted = true,
                    encryptionCipher = "AES-256-GCM",
                    attachmentsRaw = "Series_B_Term_Sheet_Draft.pdf|1.8 MB|pdf",
                    suggestedRepliesRaw = "Tomorrow at 10:30 AM PT works great;Thank you! Looking forward to reviewing;Our legal team will review first",
                    summaryCardType = "EVENT",
                    summaryCardDataRaw = "title=Series B Term Sheet Review;date=Tomorrow, 10:30 AM - 11:30 AM PT;loc=Google Meet (meet.google.com/apx-term-rev);host=Marcus Thorne (Apex Capital);count=5;rsvp=Accepted"
                ),

                // 3. Travel Reservation Summary Card: Flight Confirmation
                EmailEntity(
                    id = "msg_3",
                    sender = "SkyWings Airlines",
                    senderEmail = "reservations@skywings.aero",
                    recipientEmail = "alex.mercer@betamail.io",
                    subject = "Flight Confirmation: SW-842 to San Francisco (SFO) - Terminal 2",
                    bodySnippet = "Flight Itinerary & Digital Boarding Pass: Booking Reference BK-9942A. Departure Tomorrow, 08:30 AM Gate B14, Seat 4A...",
                    body = """Flight Itinerary & Digital Boarding Pass

Booking Reference: BK-9942A
Passenger: Alex Mercer
Flight: SW-842 Non-stop
Departure: Tomorrow, 08:30 AM (Terminal 2, Gate B14)
Arrival: Tomorrow, 11:45 AM (SFO Terminal 3)
Seat: 4A (Priority Boarding Zone 1)

Please check in online 24 hours prior or present this QR pass at the security checkpoint. Carry-on allowance: 1 personal item + 1 carry-on suitcase.

Safe travels!""",
                    timestamp = now - (3 * hour),
                    isRead = true,
                    aiSummary = "Flight SW-842 departing tomorrow at 8:30 AM from Terminal 2 Gate B14, Seat 4A. Zone 1 priority boarding.",
                    aiPriorityScore = 84,
                    aiPriorityReason = "Upcoming travel in < 24 hours with gate and check-in details.",
                    category = EmailCategory.UPDATES.name,
                    labelsRaw = "Travel, Urgent",
                    isStarred = false,
                    isArchived = false,
                    isDeleted = false,
                    isEncrypted = false,
                    encryptionCipher = "",
                    attachmentsRaw = "Mobile_Boarding_Pass.pkpass|120 KB|pkpass",
                    suggestedRepliesRaw = "Add to Calendar;Check in now;Share itinerary with contact",
                    summaryCardType = "TRAVEL",
                    summaryCardDataRaw = "airline=SkyWings Aero;flight=SW-842;from=JFK (Terminal 2);to=SFO (Terminal 3);depTime=Tomorrow, 08:30 AM;arrTime=Tomorrow, 11:45 AM;gate=B14;seat=4A;group=Zone 1 Priority;ref=BK-9942A"
                ),

                // 4. Bill Payment Summary Card: Stripe Cloud Services
                EmailEntity(
                    id = "msg_4",
                    sender = "Stripe Billing",
                    senderEmail = "notifications@stripe.com",
                    recipientEmail = "alex.mercer@betamail.io",
                    subject = "Invoice #INV-2026-8819 for Cloud Infrastructure ($149.00 USD)",
                    bodySnippet = "Your automated subscription payment of $149.00 USD for Beta Cloud Enterprise infrastructure was processed on card ending in 4022...",
                    body = """Hi there,

Your automated subscription payment of $149.00 USD for Beta Cloud Enterprise infrastructure was successfully processed on your card ending in 4022.

A copy of your tax invoice with breakdown is available for your accounting records.

Summary:
- Period: Nov 1 - Nov 30, 2026
- Plan: Enterprise Dedicated Nodes (3x)
- Subtotal: $149.00
- Tax: $0.00
- Total Paid: $149.00 USD

Thank you for your business!""",
                    timestamp = now - (5 * hour),
                    isRead = true,
                    aiSummary = "Monthly cloud infrastructure payment of $149.00 USD successfully processed for Nov 2026.",
                    aiPriorityScore = 52,
                    aiPriorityReason = "Informational financial receipt with no immediate action required.",
                    category = EmailCategory.UPDATES.name,
                    labelsRaw = "Finance",
                    isStarred = false,
                    isArchived = false,
                    isDeleted = false,
                    isEncrypted = false,
                    encryptionCipher = "",
                    attachmentsRaw = "Invoice_INV_2026_8819.pdf|180 KB|pdf",
                    suggestedRepliesRaw = "Forward to accountant;Download PDF invoice;View billing dashboard",
                    summaryCardType = "BILL",
                    summaryCardDataRaw = "vendor=Stripe Cloud Infrastructure;amount=$149.00 USD;dueDate=Nov 30, 2026;invoice=INV-2026-8819;status=Paid Automatically"
                ),

                // 5. Package Tracking Summary Card: Nordic Gear Expedition Pack
                EmailEntity(
                    id = "msg_5",
                    sender = "Nordic Gear Logistics",
                    senderEmail = "shipping@nordicgear.com",
                    recipientEmail = "alex.mercer@betamail.io",
                    subject = "Your Package is Out for Delivery! Track #1Z9999999999999999",
                    bodySnippet = "Great news! Your ultralight expedition waterproof trekking pack is out for delivery today with UPS Express...",
                    body = """Your Order #NG-7741 is on the delivery truck!

Carrier: UPS Express
Tracking Number: 1Z9999999999999999
Estimated Delivery: Today by 7:00 PM
Item: Ultralight 45L Waterproof Expedition Backpack

Please ensure someone is available at the delivery location or authorize front door dropoff.""",
                    timestamp = now - (7 * hour),
                    isRead = false,
                    aiSummary = "UPS package out for delivery today by 7:00 PM containing Ultralight 45L Expedition Backpack.",
                    aiPriorityScore = 72,
                    aiPriorityReason = "Package delivery happening today within hours.",
                    category = EmailCategory.UPDATES.name,
                    labelsRaw = "Travel",
                    isStarred = false,
                    isArchived = false,
                    isDeleted = false,
                    isEncrypted = false,
                    encryptionCipher = "",
                    attachmentsRaw = "Shipping_Label_Barcode.pdf|210 KB|pdf",
                    suggestedRepliesRaw = "Track on UPS;Authorize drop-off;Contact support",
                    summaryCardType = "PACKAGE",
                    summaryCardDataRaw = "tracking=1Z9999999999999999;carrier=UPS Express;status=Out for Delivery;delivery=Today by 7:00 PM;desc=Ultralight 45L Expedition Pack"
                ),

                // 6. Forums / Community Tab Email
                EmailEntity(
                    id = "msg_6",
                    sender = "Kotlin Multiplatform Forum",
                    senderEmail = "digest@kotlinlang.discourse.group",
                    recipientEmail = "alex.mercer@betamail.io",
                    subject = "[KMP Discussion] Best Practices for Room Database Encryption & Sharding",
                    bodySnippet = "@romain-dev: In our latest benchmarks across Android 15 & 16, using SQLCipher with AES-256-GCM yielded 4x faster throughput...",
                    body = """Kotlin Developers Community Digest

Top Discussions this week:
- Best Practices for Room Database Encryption & Zero-Trust Local Storage
- Jetpack Compose Adaptive Navigation for Foldables and Tablets
- How Small Language Models (Gemini Nano) integrate with local Room entities

Click below to participate in the conversation.""",
                    timestamp = now - (10 * hour),
                    isRead = false,
                    aiSummary = "Forum discussion digest on Room database encryption benchmarks and adaptive Compose layouts.",
                    aiPriorityScore = 40,
                    aiPriorityReason = "Community forum discussion update.",
                    category = EmailCategory.FORUMS.name,
                    labelsRaw = "Work",
                    isStarred = false,
                    isArchived = false,
                    isDeleted = false,
                    isEncrypted = false,
                    encryptionCipher = "",
                    attachmentsRaw = "",
                    suggestedRepliesRaw = "Open discussion;Mute thread"
                ),

                // 7. Social Tab Email
                EmailEntity(
                    id = "msg_7",
                    sender = "LinkedIn Notifications",
                    senderEmail = "messages-noreply@linkedin.com",
                    recipientEmail = "alex.mercer@betamail.io",
                    subject = "Sarah Connor and 4 others viewed your AI Infrastructure profile",
                    bodySnippet = "See who is looking at your profile this week: Sarah Connor (VP Engineering at Robotics Tech) viewed your credentials...",
                    body = """You appeared in 42 searches this week!

People from OpenAI, Google DeepMind, and Apex Capital viewed your profile.
See your full analytics and connection recommendations inside the app.""",
                    timestamp = now - (14 * hour),
                    isRead = true,
                    aiSummary = "Weekly profile viewer digest from LinkedIn showing 42 search appearances.",
                    aiPriorityScore = 30,
                    aiPriorityReason = "Social network notification.",
                    category = EmailCategory.SOCIAL.name,
                    labelsRaw = "Work",
                    isStarred = false,
                    isArchived = false,
                    isDeleted = false,
                    isEncrypted = false,
                    encryptionCipher = "",
                    attachmentsRaw = "",
                    suggestedRepliesRaw = "View profile;Connect with Sarah"
                ),

                // 8. Promotions Tab with Mass Unsubscribe capability
                EmailEntity(
                    id = "msg_8",
                    sender = "Nordic Gear Deals",
                    senderEmail = "deals@nordicgear.com",
                    recipientEmail = "alex.mercer@betamail.io",
                    subject = "Early Access: 40% Off Ultralight Tents & Trekking Equipment",
                    bodySnippet = "Exclusive early VIP access! Get 40% off our award-winning waterproof trekking backpacks with coupon VIP-BETA...",
                    body = """Exclusive early VIP access!

Get 40% off our award-winning waterproof trekking backpacks and all-weather tents before public release. Use code VIP-BETA at checkout.

Offer expires in 48 hours while inventory lasts.
You received this email because you subscribed to Nordic Gear promotional bulletins.""",
                    timestamp = now - (18 * hour),
                    isRead = true,
                    aiSummary = "Promotional offer for 40% off expedition tents and gear with code VIP-BETA.",
                    aiPriorityScore = 20,
                    aiPriorityReason = "Promotional marketing offer with coupon code.",
                    category = EmailCategory.PROMOTIONS.name,
                    labelsRaw = "Travel",
                    isStarred = false,
                    isArchived = false,
                    isDeleted = false,
                    isEncrypted = false,
                    encryptionCipher = "",
                    attachmentsRaw = "",
                    suggestedRepliesRaw = "Shop now;Unsubscribe",
                    mailingListSender = "deals@nordicgear.com"
                ),

                // 9. Confidential Mode Email Example
                EmailEntity(
                    id = "msg_9",
                    sender = "Apex Legal Counsel",
                    senderEmail = "legal@apexvc.com",
                    recipientEmail = "alex.mercer@betamail.io",
                    subject = "[CONFIDENTIAL] M&A Indemnification Schedule & Shareholder Cap Table",
                    bodySnippet = "CONFIDENTIAL TRANSMISSION: This message expires in 7 days. Forwarding, copying, downloading, and printing are disabled...",
                    body = """CONFIDENTIAL LEGAL TRANSMISSION

This email and its contents are subject to strict non-disclosure obligations. 

Confidential controls active:
- Expiration date: Nov 16, 2026 (7 days from delivery)
- Forwarding, copying, screenshotting, downloading, and printing are permanently disabled.
- SMS passcode verification required for external recipients.

Please review the attached schedule directly in the secure viewer.""",
                    timestamp = now - (20 * hour),
                    isRead = false,
                    aiSummary = "Confidential legal transmission of shareholder cap table expiring in 7 days with forwarding/copying disabled.",
                    aiPriorityScore = 88,
                    aiPriorityReason = "Confidential legal correspondence with strict non-disclosure controls.",
                    category = EmailCategory.PRIMARY.name,
                    labelsRaw = "Urgent, Finance, Work, Encrypted",
                    isStarred = true,
                    isArchived = false,
                    isDeleted = false,
                    isEncrypted = true,
                    encryptionCipher = "AES-256-GCM + Ephemeral Key",
                    attachmentsRaw = "Cap_Table_Confidential.pdf|3.4 MB|pdf",
                    suggestedRepliesRaw = "Acknowledged and reviewed;Access confirmed",
                    isConfidential = true,
                    confidentialExpiration = "Expires Nov 16, 2026 (in 7 days)",
                    requiresSmsPasscode = true,
                    smsPasscode = "8842"
                ),

                // 10. Spam & Malicious Phishing Filter Example
                EmailEntity(
                    id = "msg_10",
                    sender = "Security-Alerts-Urgent",
                    senderEmail = "security-update@crypto-wallet-verify.xyz",
                    recipientEmail = "alex.mercer@betamail.io",
                    subject = "WARNING: Immediate Account Verification Required or Wallet Freeze",
                    bodySnippet = "Your account will be suspended in 24 hours unless you click here to verify your private recovery keys immediately...",
                    body = """URGENT NOTICE

Your account security credentials need immediate synchronization. Click the link below to input your recovery seed phrase immediately or access will be restricted permanently.

BETA MAIL automated security filters classified this as Phishing/Malicious.""",
                    timestamp = now - (26 * hour),
                    isRead = true,
                    aiSummary = "Phishing alert attempting credential harvesting. Automatically quarantined by Beta Mail spam filters.",
                    aiPriorityScore = 5,
                    aiPriorityReason = "Suspicious sender domain and deceptive credential harvesting attempt.",
                    category = EmailCategory.SPAM.name,
                    labelsRaw = "Spam",
                    isStarred = false,
                    isArchived = true,
                    isDeleted = false,
                    isEncrypted = false,
                    encryptionCipher = "",
                    attachmentsRaw = "",
                    suggestedRepliesRaw = "",
                    isSpam = true
                )
            )
        }
    }
}
