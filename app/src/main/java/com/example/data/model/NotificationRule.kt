package com.example.data.model

data class NotificationRule(
    val minimumPriorityScore: Int = 75,
    val notifyOnlyVips: Boolean = false,
    val vipSenders: List<String> = listOf("sarah.connor@ai-robotics.tech", "ceo@apexsystems.com", "security@vault-auth.org"),
    val notifyUrgentLabelsOnly: Boolean = false,
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: String = "22:00",
    val quietHoursEnd: String = "07:00",
    val soundVibrateEnabled: Boolean = true
)

data class CloudBackupInfo(
    val isE2EEncryptionEnabled: Boolean = true,
    val cipherSuite: String = "AES-256-GCM + X25519",
    val lastBackupTimestamp: Long = System.currentTimeMillis() - 1000 * 60 * 60 * 4, // 4 hours ago
    val encryptedRecordCount: Int = 18,
    val backupHash: String = "SHA256: 4f8b2c91a0de49bb3c88e91823f66d40",
    val cloudRegion: String = "Global Sovereign Encrypted Cluster (Offline Cached)"
)
