package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DisplayDensity
import com.example.data.model.EmailEntity
import com.example.data.model.SummaryCard
import com.example.ui.theme.EncryptedGreen
import com.example.ui.theme.PriorityHighPink
import com.example.ui.theme.PriorityLowBlue
import com.example.ui.theme.PriorityMediumAmber
import com.example.ui.theme.StarGold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EmailListItem(
    email: EmailEntity,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    density: DisplayDensity = DisplayDensity.DEFAULT,
    onClick: () -> Unit,
    onToggleSelect: () -> Unit,
    onToggleStar: () -> Unit,
    onArchive: () -> Unit,
    onDelete: () -> Unit,
    onSnooze: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onArchive()
                    true
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                    true
                }
                SwipeToDismissBoxValue.Settled -> false
            }
        }
    )

    val itemPadding = when (density) {
        DisplayDensity.COMPACT -> 8.dp
        DisplayDensity.COMFORTABLE -> 14.dp
        DisplayDensity.DEFAULT -> 10.dp
    }

    val avatarSize = when (density) {
        DisplayDensity.COMPACT -> 32.dp
        DisplayDensity.COMFORTABLE -> 42.dp
        DisplayDensity.DEFAULT -> 38.dp
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val color by animateColorAsState(
                targetValue = when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.StartToEnd -> Color(0xFF10B981)
                    SwipeToDismissBoxValue.EndToStart -> Color(0xFFEF4444)
                    SwipeToDismissBoxValue.Settled -> MaterialTheme.colorScheme.surfaceVariant
                },
                label = "swipe_color"
            )

            val scale by animateFloatAsState(
                if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) 0.8f else 1.2f,
                label = "swipe_scale"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(color)
                    .padding(horizontal = 24.dp),
                contentAlignment = if (direction == SwipeToDismissBoxValue.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                if (direction == SwipeToDismissBoxValue.StartToEnd) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.scale(scale)
                    ) {
                        Icon(Icons.Default.Archive, contentDescription = "Archive", tint = Color.White)
                        Text("Archive", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.scale(scale)
                    ) {
                        Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                    }
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = if (density == DisplayDensity.COMPACT) 2.dp else 4.dp)
            .testTag("email_item_${email.id}")
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                } else if (!email.isRead) {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                } else {
                    MaterialTheme.colorScheme.surface
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (isSelectionMode) onToggleSelect() else onClick()
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(itemPadding)
            ) {
                // Header row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar / Selection box
                    Box(
                        modifier = Modifier
                            .size(avatarSize)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else getAvatarColor(email.senderName)
                            )
                            .clickable { onToggleSelect() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Text(
                                text = email.senderName.take(1).uppercase(Locale.getDefault()),
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = if (density == DisplayDensity.COMPACT) 13.sp else 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = email.senderName,
                                fontWeight = if (!email.isRead) FontWeight.Bold else FontWeight.SemiBold,
                                fontSize = if (density == DisplayDensity.COMPACT) 13.sp else 15.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f, fill = false)
                            )

                            if (email.isEncrypted) {
                                Icon(Icons.Default.Lock, contentDescription = "Encrypted", tint = EncryptedGreen, modifier = Modifier.size(13.dp))
                            }
                            if (email.isConfidential) {
                                Icon(Icons.Default.Shield, contentDescription = "Confidential", tint = PriorityHighPink, modifier = Modifier.size(13.dp))
                            }
                            if (email.isSnoozed) {
                                Icon(Icons.Default.Alarm, contentDescription = "Snoozed", tint = PriorityMediumAmber, modifier = Modifier.size(13.dp))
                            }
                        }

                        Text(
                            text = formatTimeAgo(email.timestamp),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Priority Badge
                    val priorityColor = when {
                        email.aiPriorityScore >= 80 -> PriorityHighPink
                        email.aiPriorityScore >= 60 -> PriorityMediumAmber
                        else -> PriorityLowBlue
                    }

                    Surface(
                        color = priorityColor.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = priorityColor, modifier = Modifier.size(10.dp))
                            Text(
                                text = "${email.aiPriorityScore}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = priorityColor
                            )
                        }
                    }

                    // Snooze Action button
                    IconButton(
                        onClick = onSnooze,
                        modifier = Modifier.size(32.dp).testTag("snooze_button_${email.id}")
                    ) {
                        Icon(Icons.Default.Alarm, contentDescription = "Snooze", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
                    }

                    // Star Button
                    IconButton(
                        onClick = onToggleStar,
                        modifier = Modifier.size(32.dp).testTag("star_button_${email.id}")
                    ) {
                        Icon(
                            imageVector = if (email.isStarred) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = if (email.isStarred) "Starred" else "Not starred",
                            tint = if (email.isStarred) StarGold else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Subject
                Text(
                    text = email.subject,
                    fontWeight = if (!email.isRead) FontWeight.Bold else FontWeight.Medium,
                    fontSize = if (density == DisplayDensity.COMPACT) 13.sp else 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Body snippet
                if (density != DisplayDensity.COMPACT || email.bodySnippet.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = email.bodySnippet.ifBlank { email.body.replace("\n", " ") },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = if (density == DisplayDensity.COMFORTABLE) 3 else 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Summary Card Quick Pill
                val summaryCard = email.summaryCard
                if (summaryCard != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val (icon, text) = when (summaryCard) {
                                is SummaryCard.PackageTracking -> Icons.Default.LocalShipping to "📦 ${summaryCard.status} • Delivery: ${summaryCard.deliveryDate}"
                                is SummaryCard.BillPayment -> Icons.Default.Payment to "💳 ${summaryCard.amountDue} due for ${summaryCard.vendor}"
                                is SummaryCard.TravelReservation -> Icons.Default.FlightTakeoff to "✈️ Gate ${summaryCard.gate} • ${summaryCard.departureAirport} ➔ ${summaryCard.arrivalAirport}"
                                is SummaryCard.EventItinerary -> Icons.Default.CalendarMonth to "📅 ${summaryCard.title} (${summaryCard.dateTime})"
                            }
                            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                            Text(text, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }

                // Follow-up Nudges banner
                if (email.nudgeText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = PriorityHighPink.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Reply, contentDescription = null, tint = PriorityHighPink, modifier = Modifier.size(12.dp))
                                Text(email.nudgeText, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PriorityHighPink)
                            }
                            Text("Reply Now ➔", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PriorityHighPink)
                        }
                    }
                }

                // Labels & Attachments
                val displayLabels = email.labels
                val displayAttachments = email.attachments

                if (displayLabels.isNotEmpty() || displayAttachments.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        displayLabels.take(3).forEach { label ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        if (displayAttachments.isNotEmpty()) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(Icons.Default.Attachment, contentDescription = "Attachment", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(11.dp))
                                    Text(
                                        text = displayAttachments.first().name.take(14) + if (displayAttachments.size > 1) " (+${displayAttachments.size - 1})" else "",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getAvatarColor(name: String): Color {
    val colors = listOf(
        Color(0xFF3B82F6),
        Color(0xFF10B981),
        Color(0xFF8B5CF6),
        Color(0xFFEC4899),
        Color(0xFFF59E0B),
        Color(0xFF06B6D4)
    )
    val hash = kotlin.math.abs(name.hashCode())
    return colors[hash % colors.size]
}

fun formatTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val minutes = diff / (60 * 1000)
    val hours = diff / (60 * 60 * 1000)
    val days = diff / (24 * 60 * 60 * 1000)

    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days < 7 -> "${days}d ago"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
    }
}
