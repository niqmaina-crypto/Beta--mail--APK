package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val emailAddress: String = "user@betamail.io",
    val displayName: String = "User",
    val passwordHash: String = "••••••••••••",
    val profilePicture: String = "avatar_indigo", // avatar identifier or picture URI
    val createdAt: Long = System.currentTimeMillis(),
    val isConfigured: Boolean = true
)
