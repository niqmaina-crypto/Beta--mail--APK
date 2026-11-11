package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfileEntity
import com.example.ui.theme.EncryptedGreen
import com.example.ui.theme.PriorityHighPink
import com.example.ui.theme.PriorityMediumAmber

data class AvatarOption(val id: String, val name: String, val colors: List<Color>, val emoji: String)

val AVATAR_OPTIONS = listOf(
    AvatarOption("avatar_cyber_executive", "Cyber Exec", listOf(Color(0xFF4F46E5), Color(0xFF06B6D4)), "⚡"),
    AvatarOption("avatar_emerald_tech", "Emerald Pro", listOf(Color(0xFF059669), Color(0xFF10B981)), "🛡️"),
    AvatarOption("avatar_rose_pilot", "Rose AI", listOf(Color(0xFFE11D48), Color(0xFFFB7185)), "🚀"),
    AvatarOption("avatar_amber_shield", "Amber Vault", listOf(Color(0xFFD97706), Color(0xFFFBBF24)), "🔒"),
    AvatarOption("avatar_neon_violet", "Neon Violet", listOf(Color(0xFF7C3AED), Color(0xFFA78BFA)), "💎")
)

@Composable
fun UserProfileDialog(
    currentProfile: UserProfileEntity?,
    onSave: (email: String, password: String, displayName: String, profilePicture: String) -> Unit,
    onDismiss: () -> Unit
) {
    var emailAddress by remember(currentProfile) {
        mutableStateOf(currentProfile?.emailAddress ?: "alex.mercer@betamail.io")
    }
    var displayName by remember(currentProfile) {
        mutableStateOf(currentProfile?.displayName ?: "Alex Mercer")
    }
    var password by remember(currentProfile) {
        mutableStateOf(currentProfile?.passwordHash ?: "BetaMail#2026!Secured")
    }
    var selectedAvatar by remember(currentProfile) {
        mutableStateOf(currentProfile?.profilePicture ?: "avatar_cyber_executive")
    }
    var passwordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = "BETA MAIL Account Profile",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Create your own email address and profile picture. Once configured, your credentials remain permanently stored in the local Room database.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Profile Picture Avatar Chooser
                Text(
                    text = "Choose Your Profile Picture / Avatar",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Avatar preview & options
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AVATAR_OPTIONS.forEach { avatar ->
                        val isSelected = selectedAvatar == avatar.id
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(avatar.colors))
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedAvatar = avatar.id }
                                .testTag("avatar_option_${avatar.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(avatar.emoji, fontSize = 22.sp)
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                        .align(Alignment.BottomEnd),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Display Name
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Display Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_profile_name"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Email Address
                OutlinedTextField(
                    value = emailAddress,
                    onValueChange = { emailAddress = it },
                    label = { Text("Your Email Address") },
                    placeholder = { Text("username@betamail.io") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_profile_email"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Email Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Email Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_profile_password"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Security persistence info card
                Surface(
                    color = EncryptedGreen.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = null,
                            tint = EncryptedGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Permanent: Stored safely in local Room database and remains across all sessions.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (emailAddress.isNotBlank()) {
                        onSave(emailAddress, password, displayName, selectedAvatar)
                    }
                },
                modifier = Modifier.testTag("button_save_profile")
            ) {
                Text("Save & Keep Credentials", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ProfileAvatarBadge(
    avatarId: String,
    displayName: String,
    sizeDp: Int = 36,
    modifier: Modifier = Modifier
) {
    val option = AVATAR_OPTIONS.find { it.id == avatarId } ?: AVATAR_OPTIONS.first()
    Box(
        modifier = modifier
            .size(sizeDp.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(option.colors)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = option.emoji,
            fontSize = (sizeDp * 0.48).sp
        )
    }
}
