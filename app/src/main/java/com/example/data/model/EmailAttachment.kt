package com.example.data.model

data class EmailAttachment(
    val name: String,
    val size: String,
    val type: String,
    val isEncrypted: Boolean = false
)
