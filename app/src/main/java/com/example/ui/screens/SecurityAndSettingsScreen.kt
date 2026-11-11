package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberTertiaryLight
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.EncryptedGreen
import com.example.ui.theme.PriorityHighPink
import com.example.ui.viewmodel.BetaMailViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityAndSettingsScreen(
    viewModel: BetaMailViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val themeMode by viewModel.appThemeMode.collectAsState()
    val isOffline by viewModel.isOfflineMode.collectAsState()
    val queuedCount by viewModel.queuedSyncCount.collectAsState()
    val backupInfo by viewModel.cloudBackupInfo.collectAsState()
    val notificationRule by viewModel.notificationRule.collectAsState()
    val emails by viewModel.displayedEmails.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var showTestNotificationDialog by remember { mutableStateOf(false) }
    var testNotificationResult by remember { mutableStateOf<String?>(null) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    if (showEditProfileDialog) {
        com.example.ui.components.UserProfileDialog(
            currentProfile = userProfile,
            onSave = { email, pass, name, pic ->
                viewModel.saveUserProfile(email, pass, name, pic)
                showEditProfileDialog = false
            },
            onDismiss = { showEditProfileDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Security, Vault & Settings", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            // 0. User Account Profile & Password Section
            SectionHeader(icon = Icons.Default.Security, title = "BETA MAIL Account & Password", tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("card_user_account_settings")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        com.example.ui.components.ProfileAvatarBadge(
                            avatarId = userProfile?.profilePicture ?: "avatar_cyber_executive",
                            displayName = userProfile?.displayName ?: "User",
                            sizeDp = 44
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(userProfile?.displayName ?: "Alex Mercer", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                text = userProfile?.emailAddress ?: "alex.mercer@betamail.io",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Password: •••••••••••• (Persisted in Room)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { showEditProfileDialog = true },
                        modifier = Modifier.fillMaxWidth().testTag("button_edit_email_password")
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit Email Address, Profile Picture & Password", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 1. End-to-End Encryption & Security Vault Section
            SectionHeader(icon = Icons.Default.Lock, title = "End-to-End Encryption & Key Vault", tint = EncryptedGreen)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.Key, contentDescription = null, tint = EncryptedGreen, modifier = Modifier.size(22.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Cipher Suite: ${backupInfo.cipherSuite}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Zero-Knowledge client-side derivation", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Vault Public Fingerprint:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                "0x4F8B • 2C91 • A0DE • 49BB • 3C88 • E918 • 23F6 • 6D40",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Biometric / PIN App Lock", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                            Text("Require authentication to open vault", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = true,
                            onCheckedChange = { /* toggle lock */ },
                            modifier = Modifier.testTag("switch_biometric_lock")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Encrypted Cloud Backup Section
            SectionHeader(icon = Icons.Default.CloudUpload, title = "Encrypted Cloud Backup", tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Encrypted snapshot with SHA-256 integrity verification. Restorable on any device without leaking private keys.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Last Backup:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()).format(Date(backupInfo.lastBackupTimestamp)),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Verified Records:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${backupInfo.encryptedRecordCount} emails", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.backupNow() },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("button_backup_now")
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Backup Now", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.backupNow()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("button_restore_backup")
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Restore", fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Custom Notification Filtering Section
            SectionHeader(icon = Icons.Default.NotificationsActive, title = "Custom Notification Filtering", tint = PriorityHighPink)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Prevent alert fatigue by delivering push notifications strictly for high-priority messages or verified VIPs.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Priority threshold slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Priority Alert Threshold:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Surface(
                            color = PriorityHighPink.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "≥ ${notificationRule.minimumPriorityScore} Score",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PriorityHighPink,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Slider(
                        value = notificationRule.minimumPriorityScore.toFloat(),
                        onValueChange = {
                            viewModel.updateNotificationRule(notificationRule.copy(minimumPriorityScore = it.toInt()))
                        },
                        valueRange = 0f..100f,
                        steps = 9,
                        modifier = Modifier.testTag("slider_priority_threshold")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // VIP Sender filter toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("VIP Senders Only", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                            Text("Always alert for CEO, Security, and Key partners", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = notificationRule.notifyOnlyVips,
                            onCheckedChange = {
                                viewModel.updateNotificationRule(notificationRule.copy(notifyOnlyVips = it))
                            },
                            modifier = Modifier.testTag("switch_notify_vips_only")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Urgent labels filter toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Require 'Urgent' or 'Action Needed'", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                            Text("Mute all newsletters, receipts, and social alerts", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = notificationRule.notifyUrgentLabelsOnly,
                            onCheckedChange = {
                                viewModel.updateNotificationRule(notificationRule.copy(notifyUrgentLabelsOnly = it))
                            },
                            modifier = Modifier.testTag("switch_notify_urgent_only")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Button to test notification filter against latest email
                    Button(
                        onClick = {
                            val targetEmail = emails.firstOrNull()
                            if (targetEmail != null) {
                                val allowed = viewModel.let { vm ->
                                    val rule = notificationRule
                                    val isVip = rule.vipSenders.any { targetEmail.senderEmail.contains(it, ignoreCase = true) }
                                    if (isVip) true
                                    else if (rule.notifyOnlyVips) false
                                    else if (rule.notifyUrgentLabelsOnly && !targetEmail.labels.any { it == "Urgent" || it == "Action Needed" }) false
                                    else targetEmail.aiPriorityScore >= rule.minimumPriorityScore
                                }
                                testNotificationResult = if (allowed) {
                                    "🔔 Notification ALLOWED!\n\nMessage: '${targetEmail.subject}'\nPriority: ${targetEmail.aiPriorityScore}/100\nMeets custom notification threshold (≥ ${notificationRule.minimumPriorityScore})."
                                } else {
                                    "🔕 Notification MUTED!\n\nMessage: '${targetEmail.subject}'\nPriority: ${targetEmail.aiPriorityScore}/100\nMuted because it did not meet your custom threshold (≥ ${notificationRule.minimumPriorityScore}) or VIP/Urgent rule."
                                }
                                showTestNotificationDialog = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("button_test_notification")
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Test Notification Filtering with Top Mail", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Offline Access Capabilities Section
            SectionHeader(icon = Icons.Default.Storage, title = "Offline Access & Local Room Storage", tint = AmberTertiaryLight)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "All messages, drafts, and AI labels are preserved on-device in SQLite/Room for instant offline viewing, search, and queued syncing.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Simulate Offline Mode", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                            Text(
                                if (isOffline) "Local Room cache active ($queuedCount changes queued)" else "Online (Connected to server)",
                                fontSize = 11.sp,
                                color = if (isOffline) AmberTertiaryLight else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isOffline,
                            onCheckedChange = { viewModel.toggleOfflineMode() },
                            modifier = Modifier.testTag("switch_offline_mode")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.syncNow() },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("button_sync_offline_cache")
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Force Sync Cache", fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. Dark Mode & Comfortable Viewing Section
            SectionHeader(icon = Icons.Default.DarkMode, title = "Dark Mode & Appearance", tint = MaterialTheme.colorScheme.tertiary)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Choose your preferred viewing mode. Dark mode features high-contrast Midnight Slate designed for comfortable late-night viewing.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ThemeOptionRow(
                        title = "System Default",
                        subtitle = "Follows Android system appearance",
                        selected = themeMode == AppThemeMode.SYSTEM,
                        onClick = { viewModel.setThemeMode(AppThemeMode.SYSTEM) },
                        tag = "theme_option_system"
                    )

                    ThemeOptionRow(
                        title = "Dark Mode (Late-Night Comfortable)",
                        subtitle = "Deep midnight slate surface, saves battery on OLED",
                        selected = themeMode == AppThemeMode.DARK,
                        onClick = { viewModel.setThemeMode(AppThemeMode.DARK) },
                        tag = "theme_option_dark"
                    )

                    ThemeOptionRow(
                        title = "Light Mode",
                        subtitle = "Clean crisp contrast for bright environments",
                        selected = themeMode == AppThemeMode.LIGHT,
                        onClick = { viewModel.setThemeMode(AppThemeMode.LIGHT) },
                        tag = "theme_option_light"
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (showTestNotificationDialog && testNotificationResult != null) {
        AlertDialog(
            onDismissRequest = { showTestNotificationDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = PriorityHighPink)
                    Text("Notification Filter Test", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Text(testNotificationResult ?: "", fontSize = 13.sp, lineHeight = 19.sp)
            },
            confirmButton = {
                TextButton(onClick = { showTestNotificationDialog = false }) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun SectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    tint: androidx.compose.ui.graphics.Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ThemeOptionRow(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            modifier = Modifier.testTag(tag)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium, fontSize = 13.sp)
            Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
