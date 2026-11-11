package com.example.data.model

enum class DisplayDensity(val label: String, val description: String) {
    DEFAULT("Default", "Standard comfortable row spacing with badges"),
    COMFORTABLE("Comfortable", "Spacious layout with preview snippets"),
    COMPACT("Compact", "High density, fits more emails on screen")
}

enum class ReadingPaneMode(val label: String, val description: String) {
    NO_SPLIT("No split", "Standard single column inbox list"),
    RIGHT_OF_INBOX("Right of inbox", "Side-by-side reading pane on large screens"),
    BELOW_INBOX("Below inbox", "Horizontal split view with message preview below")
}

data class CalendarEventItem(
    val id: String,
    val title: String,
    val timeRange: String,
    val date: String,
    val location: String,
    val isFromEmail: Boolean = false
)

data class KeepNoteItem(
    val id: String,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class TaskItem(
    val id: String,
    val title: String,
    val dueDate: String,
    val isCompleted: Boolean = false,
    val emailRefId: String? = null
)

data class ContactItem(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val isVip: Boolean = false
)

data class DelegatedAccount(
    val email: String,
    val displayName: String,
    val permissions: String = "Read & Respond (No settings access)",
    val isActive: Boolean = false
)

data class SmartPersonalizationSettings(
    val smartFeaturesEnabled: Boolean = true,
    val crossAppPersonalizationEnabled: Boolean = true, // Sharing with Google Chat and Google Meet
    val geminiSmartComposeEnabled: Boolean = true,
    val threadOverviewsEnabled: Boolean = true,
    val undoSendDelaySeconds: Int = 10, // 5, 10, 20, 30
    val density: DisplayDensity = DisplayDensity.DEFAULT,
    val readingPaneMode: ReadingPaneMode = ReadingPaneMode.NO_SPLIT
)
