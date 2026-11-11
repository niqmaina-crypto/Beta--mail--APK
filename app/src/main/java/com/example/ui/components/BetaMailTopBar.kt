package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfileEntity
import com.example.ui.theme.AmberTertiaryLight
import com.example.ui.theme.EncryptedGreen
import com.example.ui.theme.PriorityHighPink

@Composable
fun BetaMailTopBar(
    isSelectionMode: Boolean,
    selectedCount: Int,
    searchQuery: String,
    isOffline: Boolean,
    queuedCount: Int,
    userProfile: UserProfileEntity? = null,
    onSearchClick: () -> Unit,
    onClearSelection: () -> Unit,
    onBatchArchive: () -> Unit,
    onBatchDelete: () -> Unit,
    onBatchMarkRead: (Boolean) -> Unit,
    onToggleOffline: () -> Unit,
    onOpenSecuritySettings: () -> Unit,
    onProfileClick: () -> Unit = {},
    onToggleWorkspaceSidePanel: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        if (isSelectionMode) {
            // Action mode for multi-selection
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onClearSelection,
                    modifier = Modifier.testTag("button_clear_selection")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close selection")
                }

                Text(
                    text = "$selectedCount selected",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = { onBatchMarkRead(true) },
                    modifier = Modifier.testTag("button_batch_mark_read")
                ) {
                    Icon(Icons.Default.MarkEmailRead, contentDescription = "Mark as read")
                }

                IconButton(
                    onClick = onBatchArchive,
                    modifier = Modifier.testTag("button_batch_archive")
                ) {
                    Icon(Icons.Default.Archive, contentDescription = "Archive selected")
                }

                IconButton(
                    onClick = onBatchDelete,
                    modifier = Modifier.testTag("button_batch_delete")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete selected")
                }
            }
        } else {
            // Standard Search Pill & App Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clickable { onSearchClick() }
                        .testTag("search_bar_pill")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // User Profile Picture / Avatar button
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { onProfileClick() }
                                .testTag("topbar_profile_avatar"),
                            contentAlignment = Alignment.Center
                        ) {
                            ProfileAvatarBadge(
                                avatarId = userProfile?.profilePicture ?: "avatar_cyber_executive",
                                displayName = userProfile?.displayName ?: "User",
                                sizeDp = 34
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = if (searchQuery.isBlank()) "Search in BETA MAIL…" else searchQuery,
                            color = if (searchQuery.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )

                        // Offline toggle badge button
                        Surface(
                            onClick = onToggleOffline,
                            shape = RoundedCornerShape(14.dp),
                            color = if (isOffline) AmberTertiaryLight.copy(alpha = 0.2f) else EncryptedGreen.copy(alpha = 0.15f),
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .testTag("offline_toggle_pill")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (isOffline) Icons.Default.CloudOff else Icons.Default.CloudDone,
                                    contentDescription = "Offline Mode Status",
                                    tint = if (isOffline) AmberTertiaryLight else EncryptedGreen,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = if (isOffline) "OFFLINE" else "ONLINE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOffline) AmberTertiaryLight else EncryptedGreen
                                )
                            }
                        }

                        // Google Workspace Side Panel button
                        IconButton(
                            onClick = onToggleWorkspaceSidePanel,
                            modifier = Modifier
                                .size(36.dp)
                                .minimumInteractiveComponentSize()
                                .testTag("button_workspace_side_panel")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Google Workspace Side Panel",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Security & Vault button
                        IconButton(
                            onClick = onOpenSecuritySettings,
                            modifier = Modifier
                                .size(36.dp)
                                .minimumInteractiveComponentSize()
                                .testTag("button_security_settings")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Security, Encryption & Settings",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
