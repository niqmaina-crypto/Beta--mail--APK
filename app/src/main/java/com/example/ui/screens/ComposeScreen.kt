package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.BetaAISuggestion
import com.example.ai.BetaAIWritingCoach
import com.example.ui.components.ProfileAvatarBadge
import com.example.ui.components.UserProfileDialog
import com.example.ui.theme.EncryptedGreen
import com.example.ui.theme.PriorityHighPink
import com.example.ui.theme.PriorityMediumAmber
import com.example.ui.viewmodel.BetaMailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeScreen(
    viewModel: BetaMailViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val userProfile by viewModel.userProfile.collectAsState()
    var showProfileModal by remember { mutableStateOf(false) }

    var toText by remember { mutableStateOf("") }
    var subjectText by remember { mutableStateOf("") }
    var bodyText by remember { mutableStateOf("") }
    var isEncrypted by remember { mutableStateOf(true) }
    var attachedFile by remember { mutableStateOf<String?>(null) }

    // BETA AI Writing Coach State
    val aiOpinion by remember(subjectText, bodyText) {
        derivedStateOf { BetaAIWritingCoach.analyzeEmail(subjectText, bodyText) }
    }
    val aiSuggestions by remember(subjectText, bodyText) {
        derivedStateOf { BetaAIWritingCoach.getSuggestionsForContext(subjectText, bodyText) }
    }

    // Currently agreed / selected suggestion awaiting [Ctrl] key confirmation
    var agreedSuggestion by remember { mutableStateOf<BetaAISuggestion?>(null) }
    var insertFeedbackMessage by remember { mutableStateOf<String?>(null) }

    // Function to apply agreed suggestion (called when Ctrl is pressed on keyboard or clicked on UI)
    val applyAgreedSuggestion: () -> Unit = {
        agreedSuggestion?.let { sug ->
            bodyText = if (bodyText.isBlank()) {
                sug.content.trimStart()
            } else {
                bodyText.trimEnd() + sug.content
            }
            insertFeedbackMessage = "Agreed suggestion applied to draft via [Ctrl]!"
            agreedSuggestion = null
        }
    }

    if (showProfileModal) {
        UserProfileDialog(
            currentProfile = userProfile,
            onSave = { email, pass, name, pic ->
                viewModel.saveUserProfile(email, pass, name, pic)
                showProfileModal = false
            },
            onDismiss = { showProfileModal = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compose Message", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("compose_back_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Discard draft")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            attachedFile = if (attachedFile == null) "Document_Attachment.pdf|1.2 MB|pdf" else null
                        },
                        modifier = Modifier.testTag("compose_attach_button")
                    ) {
                        Icon(
                            Icons.Default.Attachment,
                            contentDescription = "Attach file",
                            tint = if (attachedFile != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Button(
                        onClick = {
                            val recipient = if (toText.isBlank()) "team@apexsystems.com" else toText
                            val subject = if (subjectText.isBlank()) "Quick Update" else subjectText
                            val body = if (bodyText.isBlank()) "Please review the attached project document." else bodyText
                            viewModel.sendEmail(
                                to = recipient,
                                subject = subject,
                                body = body,
                                isEncrypted = isEncrypted,
                                attachments = attachedFile ?: ""
                            )
                        },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("compose_send_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
            .fillMaxSize()
            // Keyboard shortcut listener: Pressing Ctrl accepts agreed suggestion!
            .onPreviewKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown &&
                    (keyEvent.key == Key.CtrlLeft || keyEvent.key == Key.CtrlRight || keyEvent.isCtrlPressed)
                ) {
                    if (agreedSuggestion != null) {
                        applyAgreedSuggestion()
                        true
                    } else false
                } else false
            }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // User Profile Header: Display created email address & profile picture
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showProfileModal = true }
                    .testTag("card_compose_profile_sender")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileAvatarBadge(
                        avatarId = userProfile?.profilePicture ?: "avatar_cyber_executive",
                        displayName = userProfile?.displayName ?: "User",
                        sizeDp = 38
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("From:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(userProfile?.displayName ?: "Alex Mercer", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Text(
                            text = "<${userProfile?.emailAddress ?: "alex.mercer@betamail.io"}>",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    IconButton(onClick = { showProfileModal = true }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit email address & password", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // End-to-End Encryption Banner Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isEncrypted) EncryptedGreen.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_e2e_toggle")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (isEncrypted) EncryptedGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = if (isEncrypted) "E2E Encryption Enabled" else "Standard Plaintext Delivery",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isEncrypted) EncryptedGreen else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isEncrypted) "AES-256-GCM cipher with zero-knowledge keys" else "Standard MIME format",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = isEncrypted,
                        onCheckedChange = { isEncrypted = it },
                        modifier = Modifier.testTag("switch_encryption")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // To Recipient
            OutlinedTextField(
                value = toText,
                onValueChange = { toText = it },
                label = { Text("To") },
                placeholder = { Text("e.g. elena@apexsystems.com") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("compose_to_input"),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subject
            OutlinedTextField(
                value = subjectText,
                onValueChange = { subjectText = it },
                label = { Text("Subject") },
                placeholder = { Text("Subject title…") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("compose_subject_input"),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            // Attachment indicator if attached
            if (attachedFile != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Attachment,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Document_Attachment.pdf (1.2 MB)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        IconButton(onClick = { attachedFile = null }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Remove attachment", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Email Message Body
            OutlinedTextField(
                value = bodyText,
                onValueChange = { bodyText = it },
                label = { Text("Message Body") },
                placeholder = { Text("Write your message here… BETA AI will evaluate your draft and offer suggestions.") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .testTag("compose_body_input"),
                shape = RoundedCornerShape(10.dp)
            )

            // Feedback badge if suggestion was just inserted via Ctrl
            AnimatedVisibility(visible = insertFeedbackMessage != null) {
                Surface(
                    color = EncryptedGreen.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EncryptedGreen, modifier = Modifier.size(16.dp))
                        Text(insertFeedbackMessage ?: "", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EncryptedGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ------------------------------------------------------------------
            // BETA AI SECTION: Opinion Coach & Suggestions with [Ctrl] Trigger
            // ------------------------------------------------------------------
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_beta_ai_panel")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = "BETA AI",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "BETA AI Writing Assistant & Opinions",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        // Tone Badge
                        Surface(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = aiOpinion.tone,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Opinion Box
                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.Psychology,
                                    contentDescription = "Opinion",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "BETA AI Opinion on Your Draft:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "${aiOpinion.clarityScore}% Clarity",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (aiOpinion.clarityScore >= 80) EncryptedGreen else PriorityMediumAmber
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = aiOpinion.summaryOpinion,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 17.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tip: ${aiOpinion.recommendation}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Suggestions Header with Ctrl instruction
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Suggestions on What to Write:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Suggestion Cards
                    aiSuggestions.forEach { suggestion ->
                        val isAgreed = agreedSuggestion?.id == suggestion.id
                        val borderColor by animateColorAsState(
                            if (isAgreed) MaterialTheme.colorScheme.primary else Color.Transparent,
                            label = "border_color"
                        )

                        Surface(
                            color = if (isAgreed) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(
                                    width = if (isAgreed) 2.dp else 1.dp,
                                    color = if (isAgreed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    agreedSuggestion = if (isAgreed) null else suggestion
                                }
                                .testTag("suggestion_item_${suggestion.id}")
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = suggestion.label,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    // Agree button
                                    OutlinedButton(
                                        onClick = {
                                            agreedSuggestion = if (isAgreed) null else suggestion
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (isAgreed) MaterialTheme.colorScheme.primary else Color.Transparent,
                                            contentColor = if (isAgreed) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                                        ),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Icon(
                                            if (isAgreed) Icons.Default.Check else Icons.Default.ThumbUp,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (isAgreed) "Agreed" else "Agree", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = suggestion.content.trim(),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    // ---------------------------------------------------------
                    // PROMINENT "PRESS CTRL" ACTION TRIGGER (Per User Brief)
                    // ---------------------------------------------------------
                    AnimatedVisibility(visible = agreedSuggestion != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { applyAgreedSuggestion() }
                                    .testTag("button_ctrl_accept_suggestion")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        // Mechanical Keycap look for Ctrl
                                        Surface(
                                            color = Color.Black,
                                            shape = RoundedCornerShape(6.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                                            modifier = Modifier.padding(end = 4.dp)
                                        ) {
                                            Text(
                                                text = " Ctrl ",
                                                color = Color.White,
                                                fontSize = 13.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.ExtraBold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            )
                                        }

                                        Column {
                                            Text(
                                                text = "You agreed with '${agreedSuggestion?.label}'!",
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "Press physical [Ctrl] key or tap this button to insert",
                                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Apply",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
