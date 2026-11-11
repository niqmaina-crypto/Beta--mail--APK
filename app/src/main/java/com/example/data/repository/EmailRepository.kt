package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.EmailDao
import com.example.data.local.UserProfileDao
import com.example.data.model.CalendarEventItem
import com.example.data.model.CloudBackupInfo
import com.example.data.model.ContactItem
import com.example.data.model.DelegatedAccount
import com.example.data.model.EmailCategory
import com.example.data.model.EmailEntity
import com.example.data.model.KeepNoteItem
import com.example.data.model.NotificationRule
import com.example.data.model.SmartPersonalizationSettings
import com.example.data.model.TaskItem
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class EmailRepository(private val context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val dao: EmailDao = db.emailDao()
    private val userProfileDao: UserProfileDao = db.userProfileDao()

    private val _isOfflineMode = MutableStateFlow(false)
    val isOfflineMode = _isOfflineMode.asStateFlow()

    private val _queuedSyncCount = MutableStateFlow(0)
    val queuedSyncCount = _queuedSyncCount.asStateFlow()

    private val _cloudBackupInfo = MutableStateFlow(CloudBackupInfo())
    val cloudBackupInfo = _cloudBackupInfo.asStateFlow()

    // Smart Features & Personalization Settings
    private val _personalizationSettings = MutableStateFlow(SmartPersonalizationSettings())
    val personalizationSettings = _personalizationSettings.asStateFlow()

    // Workspace Side Panel items
    private val _calendarEvents = MutableStateFlow(
        listOf(
            CalendarEventItem("cal_1", "Series B Term Sheet Review", "10:30 AM - 11:30 AM", "Tomorrow", "Google Meet", true),
            CalendarEventItem("cal_2", "Security Key Ring Ceremony", "2:00 PM - 3:00 PM", "Tomorrow", "Conference Room A", false),
            CalendarEventItem("cal_3", "Engineering Standup", "9:30 AM - 10:00 AM", "Friday", "Google Meet", false)
        )
    )
    val calendarEvents = _calendarEvents.asStateFlow()

    private val _keepNotes = MutableStateFlow(
        listOf(
            KeepNoteItem("note_1", "Q4 Cryptographic Milestones", "Zero-trust key derivation specs cleared. Awaiting Elena Vance final sign-off."),
            KeepNoteItem("note_2", "SFO Trip Checklist", "Carry-on baggage allowance 1 + 1. Gate B14 Terminal 2.")
        )
    )
    val keepNotes = _keepNotes.asStateFlow()

    private val _tasks = MutableStateFlow(
        listOf(
            TaskItem("task_1", "Sign Section 4.2 Security Audit", "Today by 5:00 PM", false, "msg_1"),
            TaskItem("task_2", "Review Apex Term Sheet draft", "Tomorrow by 10:00 AM", false, "msg_2"),
            TaskItem("task_3", "Check in online for flight SW-842", "Tomorrow 8:30 AM", false, "msg_3")
        )
    )
    val tasks = _tasks.asStateFlow()

    private val _contacts = MutableStateFlow(
        listOf(
            ContactItem("con_1", "Elena Vance", "elena.vance@blackmesa.tech", "Chief Information Security Officer", true),
            ContactItem("con_2", "Marcus Thorne", "partners@apexvc.com", "Managing Partner, Apex Capital", true),
            ContactItem("con_3", "Sarah Connor", "sarah.connor@ai-robotics.tech", "VP Engineering", true),
            ContactItem("con_4", "Liam Chen", "liam.chen@nexusstudio.design", "Lead Product Designer", false)
        )
    )
    val contacts = _contacts.asStateFlow()

    private val _delegatedAccounts = MutableStateFlow(
        listOf(
            DelegatedAccount("sarah.executive-assist@betamail.io", "Sarah (Chief of Staff)", "Read & Respond permitted", false),
            DelegatedAccount("audit-team@apexsystems.com", "Apex Compliance Auditor", "Read-only access", false)
        )
    )
    val delegatedAccounts = _delegatedAccounts.asStateFlow()

    fun updatePersonalizationSettings(settings: SmartPersonalizationSettings) {
        _personalizationSettings.value = settings
    }

    fun addCalendarEvent(title: String, time: String, date: String, location: String) {
        val newEvent = CalendarEventItem("cal_" + UUID.randomUUID().toString().take(6), title, time, date, location, true)
        _calendarEvents.value = listOf(newEvent) + _calendarEvents.value
    }

    fun addKeepNote(title: String, content: String) {
        val newNote = KeepNoteItem("note_" + UUID.randomUUID().toString().take(6), title, content)
        _keepNotes.value = listOf(newNote) + _keepNotes.value
    }

    fun addTask(title: String, dueDate: String, emailId: String? = null) {
        val newTask = TaskItem("task_" + UUID.randomUUID().toString().take(6), title, dueDate, false, emailId)
        _tasks.value = listOf(newTask) + _tasks.value
    }

    fun toggleTask(id: String) {
        _tasks.value = _tasks.value.map {
            if (it.id == id) it.copy(isCompleted = !it.isCompleted) else it
        }
    }

    // User Profile
    fun getUserProfile(): Flow<UserProfileEntity?> = userProfileDao.getUserProfile()

    suspend fun saveUserProfile(
        emailAddress: String,
        password: String,
        displayName: String,
        profilePicture: String
    ) {
        val existing = userProfileDao.getUserProfileSync()
        val updated = existing?.copy(
            emailAddress = emailAddress,
            passwordHash = password,
            displayName = displayName,
            profilePicture = profilePicture,
            isConfigured = true
        ) ?: UserProfileEntity(
            id = 1,
            emailAddress = emailAddress,
            passwordHash = password,
            displayName = displayName,
            profilePicture = profilePicture,
            isConfigured = true
        )
        userProfileDao.saveUserProfile(updated)
    }

    fun getAllInboxEmails(): Flow<List<EmailEntity>> = dao.getAllInboxEmails()

    fun getPriorityEmails(minScore: Int = 70): Flow<List<EmailEntity>> = dao.getPriorityEmails(minScore)

    fun getEmailsByCategory(category: String): Flow<List<EmailEntity>> = dao.getEmailsByCategory(category)

    fun getStarredEmails(): Flow<List<EmailEntity>> = dao.getStarredEmails()

    fun getArchivedEmails(): Flow<List<EmailEntity>> = dao.getArchivedEmails()

    fun getTrashEmails(): Flow<List<EmailEntity>> = dao.getTrashEmails()

    fun getSnoozedEmails(): Flow<List<EmailEntity>> = dao.getSnoozedEmails()

    fun getScheduledEmails(): Flow<List<EmailEntity>> = dao.getScheduledEmails()

    fun getSpamEmails(): Flow<List<EmailEntity>> = dao.getSpamEmails()

    fun getEmailById(id: String): Flow<EmailEntity?> = dao.getEmailById(id)

    fun searchEmails(query: String): Flow<List<EmailEntity>> = dao.searchEmails(query)

    fun getUnreadCount(): Flow<Int> = dao.getUnreadCount()

    fun getPriorityUnreadCount(minScore: Int = 70): Flow<Int> = dao.getPriorityUnreadCount(minScore)

    suspend fun toggleStar(id: String, currentStarred: Boolean) {
        dao.setStarred(id, !currentStarred)
        if (_isOfflineMode.value) {
            _queuedSyncCount.value += 1
        }
    }

    suspend fun toggleRead(id: String, currentRead: Boolean) {
        dao.setRead(id, !currentRead)
        if (_isOfflineMode.value) {
            _queuedSyncCount.value += 1
        }
    }

    suspend fun markRead(id: String, isRead: Boolean) {
        dao.setRead(id, isRead)
    }

    suspend fun archiveEmail(id: String) {
        dao.setArchived(id, true)
        if (_isOfflineMode.value) {
            _queuedSyncCount.value += 1
        }
    }

    suspend fun unarchiveEmail(id: String) {
        dao.setArchived(id, false)
    }

    suspend fun deleteEmail(id: String) {
        dao.setDeleted(id, true)
        if (_isOfflineMode.value) {
            _queuedSyncCount.value += 1
        }
    }

    suspend fun restoreEmail(id: String) {
        dao.setDeleted(id, false)
        dao.setArchived(id, false)
        dao.setSpam(id, false)
    }

    suspend fun snoozeEmail(id: String, untilTimestamp: Long) {
        dao.snoozeEmail(id, untilTimestamp)
        if (_isOfflineMode.value) {
            _queuedSyncCount.value += 1
        }
    }

    suspend fun setSpam(id: String, isSpam: Boolean) {
        dao.setSpam(id, isSpam)
    }

    suspend fun unsubscribeSender(senderEmail: String) {
        dao.unsubscribeSender(senderEmail)
    }

    suspend fun emptyTrash() {
        dao.emptyTrash()
    }

    suspend fun batchArchive(ids: List<String>) {
        ids.forEach { dao.setArchived(it, true) }
        if (_isOfflineMode.value) {
            _queuedSyncCount.value += ids.size
        }
    }

    suspend fun batchDelete(ids: List<String>) {
        ids.forEach { dao.setDeleted(it, true) }
        if (_isOfflineMode.value) {
            _queuedSyncCount.value += ids.size
        }
    }

    suspend fun batchMarkRead(ids: List<String>, isRead: Boolean) {
        ids.forEach { dao.setRead(it, isRead) }
    }

    suspend fun sendEmail(
        to: String,
        subject: String,
        body: String,
        isEncrypted: Boolean,
        attachmentsRaw: String = "",
        isConfidential: Boolean = false,
        confidentialExpiration: String = "",
        requiresSmsPasscode: Boolean = false,
        isScheduled: Boolean = false,
        scheduledTimestamp: Long = 0L
    ): EmailEntity {
        val userProfile = userProfileDao.getUserProfileSync()
        val myName = userProfile?.displayName ?: "Alex Mercer"
        val myEmail = userProfile?.emailAddress ?: "alex.mercer@betamail.io"

        val isUrgent = subject.contains("urgent", ignoreCase = true) ||
                body.contains("deadline", ignoreCase = true) ||
                body.contains("asap", ignoreCase = true) ||
                subject.contains("action needed", ignoreCase = true)

        val priorityScore = when {
            isUrgent -> (85..98).random()
            isConfidential -> (80..95).random()
            isEncrypted -> (75..90).random()
            subject.contains("meeting", ignoreCase = true) -> (65..80).random()
            else -> (45..70).random()
        }

        val aiReason = when {
            isUrgent -> "High urgency detected: Tight deadline and actionable request."
            isConfidential -> "Confidential transmission with recipient copy/forward restrictions."
            isEncrypted -> "Confidential E2E encrypted transmission with sensitive content."
            subject.contains("meeting", ignoreCase = true) -> "Calendar coordination awaiting recipient schedule confirmation."
            else -> "Standard correspondence with regular delivery timeline."
        }

        val aiSummary = "Outgoing correspondence to $to regarding '$subject'. Main message: ${body.take(120)}..."

        val labelsList = mutableListOf<String>()
        if (isUrgent) labelsList.add("Urgent")
        if (isEncrypted) labelsList.add("Encrypted")
        if (isConfidential) labelsList.add("Confidential")
        labelsList.add("Work")

        val newEmail = EmailEntity(
            id = "sent_" + UUID.randomUUID().toString().take(8),
            sender = myName,
            senderEmail = myEmail,
            recipientEmail = to,
            subject = subject,
            bodySnippet = body.take(140).replace("\n", " "),
            body = body,
            timestamp = if (isScheduled) scheduledTimestamp else System.currentTimeMillis(),
            isRead = true,
            aiSummary = aiSummary,
            aiPriorityScore = priorityScore,
            aiPriorityReason = aiReason,
            category = if (isScheduled) EmailCategory.SCHEDULED.name else EmailCategory.PRIMARY.name,
            labelsRaw = labelsList.joinToString(", "),
            isStarred = false,
            isArchived = false,
            isDeleted = false,
            isEncrypted = isEncrypted,
            encryptionCipher = if (isEncrypted) "AES-256-GCM + X25519" else "",
            attachmentsRaw = attachmentsRaw,
            suggestedRepliesRaw = "Received with thanks;Will review shortly",
            isDraft = false,
            isQueuedForSync = _isOfflineMode.value,
            isConfidential = isConfidential,
            confidentialExpiration = confidentialExpiration,
            requiresSmsPasscode = requiresSmsPasscode,
            smsPasscode = if (requiresSmsPasscode) "9421" else "",
            isScheduled = isScheduled,
            scheduledTimestamp = scheduledTimestamp
        )

        dao.insertEmail(newEmail)

        if (_isOfflineMode.value) {
            _queuedSyncCount.value += 1
        }
        return newEmail
    }

    suspend fun reseedSampleData() {
        val count = dao.getTotalEmailCount()
        if (count == 0) {
            dao.insertEmails(AppDatabase.getInitialSampleEmails())
        }
        if (userProfileDao.getUserProfileSync() == null) {
            userProfileDao.saveUserProfile(
                UserProfileEntity(
                    id = 1,
                    emailAddress = "alex.mercer@betamail.io",
                    displayName = "Alex Mercer",
                    passwordHash = "BetaMail#2026!Secured",
                    profilePicture = "avatar_cyber_executive",
                    isConfigured = true
                )
            )
        }
    }

    fun toggleOfflineMode() {
        val nextState = !_isOfflineMode.value
        _isOfflineMode.value = nextState
        if (!nextState) {
            _queuedSyncCount.value = 0
        }
    }

    fun syncQueuedItems() {
        _queuedSyncCount.value = 0
    }

    fun triggerCloudBackup(): CloudBackupInfo {
        val count = 28
        val hash = "SHA256: " + UUID.randomUUID().toString().replace("-", "")
        val info = CloudBackupInfo(
            isE2EEncryptionEnabled = true,
            cipherSuite = "AES-256-GCM + X25519 (Zero-Knowledge Keyring)",
            lastBackupTimestamp = System.currentTimeMillis(),
            encryptedRecordCount = count,
            backupHash = hash,
            cloudRegion = "Global Sovereign Encrypted Cluster (Verified)"
        )
        _cloudBackupInfo.value = info
        return info
    }

    fun evaluateNotificationRule(email: EmailEntity, rule: NotificationRule): Boolean {
        if (rule.vipSenders.any { email.senderEmail.contains(it, ignoreCase = true) }) {
            return true
        }
        if (rule.notifyOnlyVips) {
            return false
        }
        if (rule.notifyUrgentLabelsOnly) {
            val hasUrgent = email.labels.any { it.equals("Urgent", ignoreCase = true) || it.equals("Action Needed", ignoreCase = true) }
            if (!hasUrgent) return false
        }
        return email.aiPriorityScore >= rule.minimumPriorityScore
    }
}
