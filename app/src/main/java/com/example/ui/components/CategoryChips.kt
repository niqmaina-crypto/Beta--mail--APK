package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ScheduleSend
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Unsubscribe
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmailCategory
import com.example.ui.theme.EncryptedGreen
import com.example.ui.theme.PriorityHighPink
import com.example.ui.theme.StarGold

@Composable
fun CategoryChips(
    selectedCategory: EmailCategory,
    priorityCount: Int,
    onCategorySelected: (EmailCategory) -> Unit,
    selectedLabel: String?,
    onLabelSelected: (String?) -> Unit,
    unreadOnly: Boolean,
    onToggleUnread: () -> Unit,
    hasAttachmentOnly: Boolean,
    onToggleAttachment: () -> Unit,
    encryptedOnly: Boolean,
    onToggleEncrypted: () -> Unit,
    onOpenMassUnsubscribe: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Main categories row: Primary, Social, Promotions, Updates, Forums, Priority, Snoozed, Scheduled, Spam
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val mainCategories = listOf(
                EmailCategory.PRIORITY,
                EmailCategory.PRIMARY,
                EmailCategory.SOCIAL,
                EmailCategory.PROMOTIONS,
                EmailCategory.UPDATES,
                EmailCategory.FORUMS,
                EmailCategory.SNOOZED,
                EmailCategory.SCHEDULED,
                EmailCategory.SPAM,
                EmailCategory.ALL
            )

            mainCategories.forEach { category ->
                val isSelected = selectedCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategorySelected(category) },
                    label = {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(category.title)
                            if (category == EmailCategory.PRIORITY && priorityCount > 0) {
                                Text(
                                    "($priorityCount)",
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else PriorityHighPink
                                )
                            }
                        }
                    },
                    leadingIcon = when (category) {
                        EmailCategory.PRIORITY -> {
                            {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "AI Priority",
                                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else PriorityHighPink,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        EmailCategory.SOCIAL -> {
                            { Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        }
                        EmailCategory.PROMOTIONS -> {
                            { Icon(Icons.Default.LocalOffer, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        }
                        EmailCategory.UPDATES -> {
                            { Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        }
                        EmailCategory.FORUMS -> {
                            { Icon(Icons.Default.Forum, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        }
                        EmailCategory.SNOOZED -> {
                            { Icon(Icons.Default.Alarm, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        }
                        EmailCategory.SCHEDULED -> {
                            { Icon(Icons.Default.ScheduleSend, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        }
                        EmailCategory.SPAM -> {
                            { Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        }
                        else -> null
                    },
                    modifier = Modifier.testTag("category_chip_${category.name.lowercase()}")
                )
            }
        }

        // Smart Labels & Sub-labels row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Unread filter
            FilterChip(
                selected = unreadOnly,
                onClick = onToggleUnread,
                label = { Text("Unread") },
                leadingIcon = if (unreadOnly) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.testTag("filter_chip_unread")
            )

            // Has attachment
            FilterChip(
                selected = hasAttachmentOnly,
                onClick = onToggleAttachment,
                label = { Text("Has Attachment") },
                leadingIcon = if (hasAttachmentOnly) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                } else null,
                modifier = Modifier.testTag("filter_chip_attachment")
            )

            // Encrypted only
            FilterChip(
                selected = encryptedOnly,
                onClick = onToggleEncrypted,
                label = { Text("E2E Encrypted") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Encrypted",
                        tint = if (encryptedOnly) EncryptedGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                },
                modifier = Modifier.testTag("filter_chip_encrypted")
            )

            // Mass unsubscribe button in Promotions or general
            AssistChip(
                onClick = onOpenMassUnsubscribe,
                label = { Text("Mass Unsubscribe", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PriorityHighPink) },
                leadingIcon = {
                    Icon(Icons.Default.Unsubscribe, contentDescription = null, tint = PriorityHighPink, modifier = Modifier.size(14.dp))
                },
                modifier = Modifier.testTag("button_open_mass_unsubscribe")
            )

            // Smart Labels & Sub-labels
            val labels = listOf("Urgent", "Action Needed", "Work/Q4-Launch", "Finance/Invoices", "Travel/Tickets", "Personal", "Read Later")
            labels.forEach { label ->
                val isLabelSelected = selectedLabel == label
                FilterChip(
                    selected = isLabelSelected,
                    onClick = { onLabelSelected(label) },
                    label = { Text(label) },
                    leadingIcon = if (label == "Urgent") {
                        { Icon(Icons.Default.Star, contentDescription = null, tint = StarGold, modifier = Modifier.size(14.dp)) }
                    } else null,
                    modifier = Modifier.testTag("smart_label_chip_${label.lowercase().replace("/", "_").replace(" ", "_")}")
                )
            }
        }
    }
}
