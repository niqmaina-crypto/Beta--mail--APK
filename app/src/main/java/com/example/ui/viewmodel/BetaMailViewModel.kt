package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.BetaAIWritingCoach
import com.example.ai.DocumentSummary
import com.example.data.model.CloudBackupInfo
import com.example.data.model.DisplayDensity
import com.example.data.model.EmailCategory
import com.example.data.model.EmailEntity
import com.example.data.model.NotificationRule
import com.example.data.model.ReadingPaneMode
import com.example.data.model.SmartPersonalizationSettings
import com.example.data.model.UserProfileEntity
import com.example.data.repository.EmailRepository
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenDestination {
    INBOX,
    EMAIL_DETAIL,
    COMPOSE,
    SEARCH,
    SETTINGS_SECURITY,
    TRASH
}

data class FilterCriteria(
    val category: EmailCategory = EmailCategory.ALL,
    val selectedLabel: String? = null,
    val unreadOnly: Boolean = false,
    val hasAttachmentOnly: Boolean = false,
    val priorityThreshold: Int = 0,
    val encryptedOnly: Boolean = false
)

data class NotificationPreviewEvent(
    val title: String,
    val body: String,
    val priorityScore: Int,
    val isVip: Boolean,
    val allowedByRule: Boolean
)

data class PendingUndo(
    val emailId: String,
    val to: String,
    val subject: String,
    val body: String,
    val isEncrypted: Boolean,
    val durationSeconds: Int,
    val timestamp: Long = System.currentTimeMillis()
)

class BetaMailViewModel(application: Application) : AndroidViewModel(application) {

    val repository = EmailRepository(application)

    val isOfflineMode = repository.isOfflineMode
    val queuedSyncCount = repository.queuedSyncCount
    val cloudBackupInfo = repository.cloudBackupInfo
    val personalizationSettings = repository.personalizationSettings

    val userProfile = repository.getUserProfile().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UserProfileEntity()
    )

    private val _showProfileDialog = MutableStateFlow(false)
    val showProfileDialog: StateFlow<Boolean> = _showProfileDialog.asStateFlow()

    private val _currentScreen = MutableStateFlow(ScreenDestination.INBOX)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    private val _selectedEmailId = MutableStateFlow<String?>(null)
    val selectedEmailId: StateFlow<String?> = _selectedEmailId.asStateFlow()

    private val _filterCriteria = MutableStateFlow(FilterCriteria())
    val filterCriteria: StateFlow<FilterCriteria> = _filterCriteria.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedEmailIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedEmailIds: StateFlow<Set<String>> = _selectedEmailIds.asStateFlow()

    private val _notificationRule = MutableStateFlow(NotificationRule())
    val notificationRule: StateFlow<NotificationRule> = _notificationRule.asStateFlow()

    private val _appThemeMode = MutableStateFlow(AppThemeMode.SYSTEM)
    val appThemeMode: StateFlow<AppThemeMode> = _appThemeMode.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    private val _notificationEvent = MutableSharedFlow<NotificationPreviewEvent>()
    val notificationEvent = _notificationEvent.asSharedFlow()

    // Undo Send State
    private val _pendingUndo = MutableStateFlow<PendingUndo?>(null)
    val pendingUndo: StateFlow<PendingUndo?> = _pendingUndo.asStateFlow()

    // Gemini Document Summary Modal State
    private val _selectedDocSummary = MutableStateFlow<DocumentSummary?>(null)
    val selectedDocSummary: StateFlow<DocumentSummary?> = _selectedDocSummary.asStateFlow()

    // Google Workspace Side Panel State
    private val _isWorkspacePanelOpen = MutableStateFlow(false)
    val isWorkspacePanelOpen: StateFlow<Boolean> = _isWorkspacePanelOpen.asStateFlow()

    // Base email stream from Room
    private val rawEmails = repository.getAllInboxEmails()

    // Filtered emails based on search query, category tabs, smart labels, and flags
    val displayedEmails: StateFlow<List<EmailEntity>> = combine(
        rawEmails,
        _filterCriteria,
        _searchQuery
    ) { emails, filter, query ->
        emails.filter { email ->
            // Query search (with natural language search support)
            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                val q = query.trim().lowercase()
                email.subject.contains(q, ignoreCase = true) ||
                        email.senderName.contains(q, ignoreCase = true) ||
                        email.senderEmail.contains(q, ignoreCase = true) ||
                        email.body.contains(q, ignoreCase = true) ||
                        email.bodySnippet.contains(q, ignoreCase = true) ||
                        email.labelsRaw.contains(q, ignoreCase = true) ||
                        email.aiPriorityReason.contains(q, ignoreCase = true) ||
                        email.category.contains(q, ignoreCase = true) ||
                        (q.contains("urgent") && email.aiPriorityScore >= 80) ||
                        (q.contains("flight") && (email.summaryCardType == "TRAVEL" || email.labelsRaw.contains("Travel"))) ||
                        (q.contains("bill") && (email.summaryCardType == "BILL" || email.labelsRaw.contains("Finance"))) ||
                        (q.contains("package") && email.summaryCardType == "PACKAGE")
            }

            // Category filter
            val matchesCategory = when (filter.category) {
                EmailCategory.ALL -> true
                EmailCategory.PRIORITY -> email.aiPriorityScore >= 70
                EmailCategory.PRIMARY -> email.category == EmailCategory.PRIMARY.name
                EmailCategory.SOCIAL -> email.category == EmailCategory.SOCIAL.name
                EmailCategory.PROMOTIONS -> email.category == EmailCategory.PROMOTIONS.name
                EmailCategory.UPDATES -> email.category == EmailCategory.UPDATES.name
                EmailCategory.FORUMS -> email.category == EmailCategory.FORUMS.name
                EmailCategory.SNOOZED -> email.isSnoozed
                EmailCategory.SCHEDULED -> email.isScheduled
                EmailCategory.SPAM -> email.isSpam
            }

            // Label filter (supporting sub-labels e.g. Work/Q4-Launch)
            val matchesLabel = filter.selectedLabel == null || email.labels.any { it.equals(filter.selectedLabel, ignoreCase = true) }

            // Flags
            val matchesUnread = !filter.unreadOnly || !email.isRead
            val matchesAttachment = !filter.hasAttachmentOnly || email.attachments.isNotEmpty()
            val matchesPriority = email.aiPriorityScore >= filter.priorityThreshold
            val matchesEncrypted = !filter.encryptedOnly || email.isEncrypted

            matchesQuery && matchesCategory && matchesLabel && matchesUnread && matchesAttachment && matchesPriority && matchesEncrypted
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadCount = repository.getUnreadCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val priorityCount = repository.getPriorityUnreadCount(70)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val trashEmails = repository.getTrashEmails()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.reseedSampleData()
        }
    }

    fun navigateTo(destination: ScreenDestination, emailId: String? = null) {
        if (emailId != null) {
            _selectedEmailId.value = emailId
            viewModelScope.launch {
                repository.markRead(emailId, true)
            }
        }
        _currentScreen.value = destination
    }

    fun navigateBack() {
        if (_selectedEmailIds.value.isNotEmpty()) {
            clearSelection()
            return
        }
        when (_currentScreen.value) {
            ScreenDestination.EMAIL_DETAIL,
            ScreenDestination.COMPOSE,
            ScreenDestination.SEARCH,
            ScreenDestination.SETTINGS_SECURITY,
            ScreenDestination.TRASH -> {
                _currentScreen.value = ScreenDestination.INBOX
            }
            ScreenDestination.INBOX -> {}
        }
    }

    fun setCategory(category: EmailCategory) {
        _filterCriteria.value = _filterCriteria.value.copy(category = category)
    }

    fun setSelectedLabel(label: String?) {
        val next = if (_filterCriteria.value.selectedLabel == label) null else label
        _filterCriteria.value = _filterCriteria.value.copy(selectedLabel = next)
    }

    fun toggleUnreadFilter() {
        val curr = _filterCriteria.value.unreadOnly
        _filterCriteria.value = _filterCriteria.value.copy(unreadOnly = !curr)
    }

    fun toggleAttachmentFilter() {
        val curr = _filterCriteria.value.hasAttachmentOnly
        _filterCriteria.value = _filterCriteria.value.copy(hasAttachmentOnly = !curr)
    }

    fun toggleEncryptedFilter() {
        val curr = _filterCriteria.value.encryptedOnly
        _filterCriteria.value = _filterCriteria.value.copy(encryptedOnly = !curr)
    }

    fun setPriorityThreshold(score: Int) {
        _filterCriteria.value = _filterCriteria.value.copy(priorityThreshold = score)
    }

    fun clearFilters() {
        _filterCriteria.value = FilterCriteria()
        _searchQuery.value = ""
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun applyNaturalLanguageSearch(prompt: String) {
        _searchQuery.value = prompt
        _currentScreen.value = ScreenDestination.SEARCH
    }

    fun toggleStar(email: EmailEntity) {
        viewModelScope.launch {
            repository.toggleStar(email.id, email.isStarred)
        }
    }

    fun toggleRead(email: EmailEntity) {
        viewModelScope.launch {
            repository.toggleRead(email.id, email.isRead)
        }
    }

    fun archiveEmail(email: EmailEntity) {
        viewModelScope.launch {
            repository.archiveEmail(email.id)
            _snackbarMessage.emit("Archived: ${email.subject.take(28)}...")
        }
    }

    fun deleteEmail(email: EmailEntity) {
        viewModelScope.launch {
            repository.deleteEmail(email.id)
            _snackbarMessage.emit("Moved to Trash: ${email.subject.take(28)}...")
            if (_selectedEmailId.value == email.id) {
                navigateBack()
            }
        }
    }

    fun snoozeEmail(email: EmailEntity, untilTimestamp: Long, label: String) {
        viewModelScope.launch {
            repository.snoozeEmail(email.id, untilTimestamp)
            _snackbarMessage.emit("Snoozed until $label")
            if (_selectedEmailId.value == email.id) {
                navigateBack()
            }
        }
    }

    fun markAsSpam(email: EmailEntity) {
        viewModelScope.launch {
            repository.setSpam(email.id, true)
            _snackbarMessage.emit("Reported as spam and quarantined")
            if (_selectedEmailId.value == email.id) {
                navigateBack()
            }
        }
    }

    fun unmarkSpam(email: EmailEntity) {
        viewModelScope.launch {
            repository.setSpam(email.id, false)
            _snackbarMessage.emit("Moved back to inbox")
        }
    }

    fun massUnsubscribeAll() {
        viewModelScope.launch {
            val senders = listOf("deals@nordicgear.com", "newsletter@devweekly.io", "marketing@saas-updates.com")
            senders.forEach { repository.unsubscribeSender(it) }
            _snackbarMessage.emit("Unsubscribed from ${senders.size} bulk mailing lists")
        }
    }

    // Gemini Document Summary Modal
    fun summarizeDocument(fileName: String) {
        _selectedDocSummary.value = BetaAIWritingCoach.summarizeDocument(fileName)
    }

    fun closeDocSummary() {
        _selectedDocSummary.value = null
    }

    // Workspace Side Panel
    fun toggleWorkspacePanel() {
        _isWorkspacePanelOpen.value = !_isWorkspacePanelOpen.value
    }

    fun closeWorkspacePanel() {
        _isWorkspacePanelOpen.value = false
    }

    fun addCalendarEventFromEmail(email: EmailEntity) {
        repository.addCalendarEvent(
            title = "Review: ${email.subject.take(30)}",
            time = "10:30 AM - 11:30 AM",
            date = "Tomorrow",
            location = "Google Meet"
        )
        viewModelScope.launch {
            _snackbarMessage.emit("Converted email to Google Calendar event")
        }
    }

    fun saveEmailToKeep(email: EmailEntity) {
        repository.addKeepNote(
            title = email.subject,
            content = "From ${email.senderName}:\n${email.body.take(200)}"
        )
        viewModelScope.launch {
            _snackbarMessage.emit("Saved email to Google Keep")
        }
    }

    fun addEmailToTasks(email: EmailEntity) {
        repository.addTask(
            title = "Action: ${email.subject}",
            dueDate = "Tomorrow, 5:00 PM",
            emailId = email.id
        )
        viewModelScope.launch {
            _snackbarMessage.emit("Added to Google Tasks with due date")
        }
    }

    // Multi-selection
    fun toggleSelectEmail(id: String) {
        val current = _selectedEmailIds.value.toMutableSet()
        if (current.contains(id)) {
            current.remove(id)
        } else {
            current.add(id)
        }
        _selectedEmailIds.value = current
    }

    fun clearSelection() {
        _selectedEmailIds.value = emptySet()
    }

    fun batchArchiveSelected() {
        val ids = _selectedEmailIds.value.toList()
        viewModelScope.launch {
            repository.batchArchive(ids)
            clearSelection()
            _snackbarMessage.emit("Archived ${ids.size} conversations")
        }
    }

    fun batchDeleteSelected() {
        val ids = _selectedEmailIds.value.toList()
        viewModelScope.launch {
            repository.batchDelete(ids)
            clearSelection()
            _snackbarMessage.emit("Moved ${ids.size} conversations to Trash")
        }
    }

    fun batchMarkReadSelected(isRead: Boolean) {
        val ids = _selectedEmailIds.value.toList()
        viewModelScope.launch {
            repository.batchMarkRead(ids, isRead)
            clearSelection()
            _snackbarMessage.emit(if (isRead) "Marked as read" else "Marked as unread")
        }
    }

    // Offline mode
    fun toggleOfflineMode() {
        repository.toggleOfflineMode()
        viewModelScope.launch {
            val status = if (repository.isOfflineMode.value) "Offline Mode Enabled (Local Room Cache Active)" else "Back Online (Syncing with Cloud)"
            _snackbarMessage.emit(status)
        }
    }

    fun syncNow() {
        repository.syncQueuedItems()
        viewModelScope.launch {
            _snackbarMessage.emit("Sync complete: All local changes backed up")
        }
    }

    // Cloud Backup
    fun backupNow(): CloudBackupInfo {
        val info = repository.triggerCloudBackup()
        viewModelScope.launch {
            _snackbarMessage.emit("Encrypted zero-knowledge backup created successfully")
        }
        return info
    }

    // Profile Management
    fun openProfileDialog() {
        _showProfileDialog.value = true
    }

    fun closeProfileDialog() {
        _showProfileDialog.value = false
    }

    fun saveUserProfile(
        emailAddress: String,
        password: String,
        displayName: String,
        profilePicture: String
    ) {
        viewModelScope.launch {
            repository.saveUserProfile(emailAddress, password, displayName, profilePicture)
            _showProfileDialog.value = false
            _snackbarMessage.emit("Email address & password saved: $emailAddress")
        }
    }

    // Settings
    fun updatePersonalizationSettings(settings: SmartPersonalizationSettings) {
        repository.updatePersonalizationSettings(settings)
        viewModelScope.launch {
            _snackbarMessage.emit("Settings and reading pane updated")
        }
    }

    fun updateNotificationRule(rule: NotificationRule) {
        _notificationRule.value = rule
        viewModelScope.launch {
            _snackbarMessage.emit("Notification filter rules updated")
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        _appThemeMode.value = mode
    }

    // Undo Send
    fun triggerUndoSend() {
        val pending = _pendingUndo.value ?: return
        viewModelScope.launch {
            repository.deleteEmail(pending.emailId)
            _pendingUndo.value = null
            _snackbarMessage.emit("Sending cancelled via Undo Send. Draft restored.")
            navigateTo(ScreenDestination.COMPOSE)
        }
    }

    fun dismissUndoSend() {
        _pendingUndo.value = null
    }

    // Send email (with Undo Send cancellation window & Scheduled Send support)
    fun sendEmail(
        to: String,
        subject: String,
        body: String,
        isEncrypted: Boolean,
        attachments: String = "",
        isConfidential: Boolean = false,
        confidentialExpiration: String = "",
        requiresSmsPasscode: Boolean = false,
        isScheduled: Boolean = false,
        scheduledTimestamp: Long = 0L
    ) {
        viewModelScope.launch {
            val sent = repository.sendEmail(
                to = to,
                subject = subject,
                body = body,
                isEncrypted = isEncrypted,
                attachmentsRaw = attachments,
                isConfidential = isConfidential,
                confidentialExpiration = confidentialExpiration,
                requiresSmsPasscode = requiresSmsPasscode,
                isScheduled = isScheduled,
                scheduledTimestamp = scheduledTimestamp
            )

            if (isScheduled) {
                _snackbarMessage.emit("Email scheduled for future delivery")
                _currentScreen.value = ScreenDestination.INBOX
            } else {
                val window = personalizationSettings.value.undoSendDelaySeconds
                _pendingUndo.value = PendingUndo(
                    emailId = sent.id,
                    to = to,
                    subject = subject,
                    body = body,
                    isEncrypted = isEncrypted,
                    durationSeconds = window
                )
                val encryptionNote = if (isEncrypted) " [E2E Encrypted]" else ""
                _snackbarMessage.emit("Email sent to $to$encryptionNote (Undo available for ${window}s)")
                _currentScreen.value = ScreenDestination.INBOX

                // Timer to automatically clear pending undo
                launch {
                    delay(window * 1000L)
                    if (_pendingUndo.value?.emailId == sent.id) {
                        _pendingUndo.value = null
                    }
                }
            }
        }
    }
}
