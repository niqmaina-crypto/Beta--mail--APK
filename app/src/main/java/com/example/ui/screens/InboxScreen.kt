package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmailCategory
import com.example.ui.components.BetaMailTopBar
import com.example.ui.components.CategoryChips
import com.example.ui.components.EmailListItem
import com.example.ui.components.OfflineSyncBanner
import com.example.ui.theme.PriorityHighPink
import com.example.ui.viewmodel.BetaMailViewModel
import com.example.ui.viewmodel.ScreenDestination

@Composable
fun InboxScreen(
    viewModel: BetaMailViewModel,
    modifier: Modifier = Modifier
) {
    val emails by viewModel.displayedEmails.collectAsState()
    val filterCriteria by viewModel.filterCriteria.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedEmailIds by viewModel.selectedEmailIds.collectAsState()
    val priorityCount by viewModel.priorityCount.collectAsState()
    val isOffline by viewModel.isOfflineMode.collectAsState()
    val queuedCount by viewModel.queuedSyncCount.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val showProfileDialog by viewModel.showProfileDialog.collectAsState()

    val isSelectionMode = selectedEmailIds.isNotEmpty()

    if (showProfileDialog) {
        com.example.ui.components.UserProfileDialog(
            currentProfile = userProfile,
            onSave = { email, pass, name, pic ->
                viewModel.saveUserProfile(email, pass, name, pic)
            },
            onDismiss = { viewModel.closeProfileDialog() }
        )
    }

    Scaffold(
        topBar = {
            BetaMailTopBar(
                isSelectionMode = isSelectionMode,
                selectedCount = selectedEmailIds.size,
                searchQuery = searchQuery,
                isOffline = isOffline,
                queuedCount = queuedCount,
                userProfile = userProfile,
                onSearchClick = { viewModel.navigateTo(ScreenDestination.SEARCH) },
                onClearSelection = { viewModel.clearSelection() },
                onBatchArchive = { viewModel.batchArchiveSelected() },
                onBatchDelete = { viewModel.batchDeleteSelected() },
                onBatchMarkRead = { viewModel.batchMarkReadSelected(it) },
                onToggleOffline = { viewModel.toggleOfflineMode() },
                onOpenSecuritySettings = { viewModel.navigateTo(ScreenDestination.SETTINGS_SECURITY) },
                onProfileClick = { viewModel.openProfileDialog() }
            )
        },
        floatingActionButton = {
            if (!isSelectionMode) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.navigateTo(ScreenDestination.COMPOSE) },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Compose", fontWeight = FontWeight.Bold) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("fab_compose")
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Category & Smart label chips
            CategoryChips(
                selectedCategory = filterCriteria.category,
                priorityCount = priorityCount,
                onCategorySelected = { viewModel.setCategory(it) },
                selectedLabel = filterCriteria.selectedLabel,
                onLabelSelected = { viewModel.setSelectedLabel(it) },
                unreadOnly = filterCriteria.unreadOnly,
                onToggleUnread = { viewModel.toggleUnreadFilter() },
                hasAttachmentOnly = filterCriteria.hasAttachmentOnly,
                onToggleAttachment = { viewModel.toggleAttachmentFilter() },
                encryptedOnly = filterCriteria.encryptedOnly,
                onToggleEncrypted = { viewModel.toggleEncryptedFilter() }
            )

            // Offline sync banner
            OfflineSyncBanner(
                isOffline = isOffline,
                queuedCount = queuedCount,
                onSyncNow = { viewModel.syncNow() },
                onToggleOffline = { viewModel.toggleOfflineMode() }
            )

            // Priority AI Overview Banner when viewing Priority Inbox
            if (filterCriteria.category == EmailCategory.PRIORITY && emails.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "AI Priority Inbox Active",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Sorted by urgency, action required, and deadline heuristics.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            if (emails.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(32.dp),
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                        Text(
                            text = "All Caught Up!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "No emails match your current category or search filters.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("inbox_email_list"),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp)
                ) {
                    items(emails, key = { it.id }) { email ->
                        EmailListItem(
                            email = email,
                            isSelected = selectedEmailIds.contains(email.id),
                            isSelectionMode = isSelectionMode,
                            onClick = { viewModel.navigateTo(ScreenDestination.EMAIL_DETAIL, email.id) },
                            onToggleSelect = { viewModel.toggleSelectEmail(email.id) },
                            onToggleStar = { viewModel.toggleStar(email) },
                            onArchive = { viewModel.archiveEmail(email) },
                            onDelete = { viewModel.deleteEmail(email) }
                        )
                    }
                }
            }
        }
    }
}
