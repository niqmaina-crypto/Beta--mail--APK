package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.EmailEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for storing and querying email metadata,
 * including sender, subject, body snippet, timestamp, and 'isRead' boolean flag,
 * plus categories, snooze, scheduled, spam, and mass unsubscribe.
 */
@Dao
interface EmailDao {

    @Query("""
        SELECT * FROM emails 
        WHERE isArchived = 0 AND isDeleted = 0 AND isSpam = 0 AND isScheduled = 0
        ORDER BY timestamp DESC
    """)
    fun getAllInboxEmails(): Flow<List<EmailEntity>>

    @Query("SELECT * FROM emails ORDER BY timestamp DESC")
    fun getAllEmailsByTimestamp(): Flow<List<EmailEntity>>

    @Query("SELECT * FROM emails WHERE isRead = :isRead ORDER BY timestamp DESC")
    fun getEmailsByReadStatus(isRead: Boolean): Flow<List<EmailEntity>>

    @Query("""
        SELECT * FROM emails 
        WHERE isArchived = 0 AND isDeleted = 0 AND isSpam = 0 AND isScheduled = 0 AND aiPriorityScore >= :minScore 
        ORDER BY aiPriorityScore DESC, timestamp DESC
    """)
    fun getPriorityEmails(minScore: Int = 70): Flow<List<EmailEntity>>

    @Query("""
        SELECT * FROM emails 
        WHERE isArchived = 0 AND isDeleted = 0 AND isSpam = 0 AND isScheduled = 0 AND category = :category 
        ORDER BY timestamp DESC
    """)
    fun getEmailsByCategory(category: String): Flow<List<EmailEntity>>

    @Query("SELECT * FROM emails WHERE isDeleted = 0 AND isStarred = 1 ORDER BY timestamp DESC")
    fun getStarredEmails(): Flow<List<EmailEntity>>

    @Query("SELECT * FROM emails WHERE isArchived = 1 AND isDeleted = 0 ORDER BY timestamp DESC")
    fun getArchivedEmails(): Flow<List<EmailEntity>>

    @Query("SELECT * FROM emails WHERE isDeleted = 1 ORDER BY timestamp DESC")
    fun getTrashEmails(): Flow<List<EmailEntity>>

    @Query("SELECT * FROM emails WHERE isDeleted = 0 AND snoozeUntil > :now ORDER BY snoozeUntil ASC")
    fun getSnoozedEmails(now: Long = System.currentTimeMillis()): Flow<List<EmailEntity>>

    @Query("SELECT * FROM emails WHERE isDeleted = 0 AND isScheduled = 1 ORDER BY scheduledTimestamp ASC")
    fun getScheduledEmails(): Flow<List<EmailEntity>>

    @Query("SELECT * FROM emails WHERE isDeleted = 0 AND isSpam = 1 ORDER BY timestamp DESC")
    fun getSpamEmails(): Flow<List<EmailEntity>>

    @Query("SELECT * FROM emails WHERE id = :id")
    fun getEmailById(id: String): Flow<EmailEntity?>

    @Query("SELECT * FROM emails WHERE id = :id")
    suspend fun getEmailByIdSync(id: String): EmailEntity?

    @Query("""
        SELECT * FROM emails 
        WHERE isDeleted = 0 AND (
            subject LIKE '%' || :query || '%' 
            OR sender LIKE '%' || :query || '%' 
            OR senderEmail LIKE '%' || :query || '%' 
            OR bodySnippet LIKE '%' || :query || '%'
            OR body LIKE '%' || :query || '%' 
            OR labelsRaw LIKE '%' || :query || '%'
            OR aiPriorityReason LIKE '%' || :query || '%'
            OR category LIKE '%' || :query || '%'
        )
        ORDER BY timestamp DESC
    """)
    fun searchEmails(query: String): Flow<List<EmailEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmail(email: EmailEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmails(emails: List<EmailEntity>)

    @Update
    suspend fun updateEmail(email: EmailEntity)

    @Query("UPDATE emails SET isStarred = :isStarred WHERE id = :id")
    suspend fun setStarred(id: String, isStarred: Boolean)

    @Query("UPDATE emails SET isRead = :isRead WHERE id = :id")
    suspend fun setRead(id: String, isRead: Boolean)

    @Query("UPDATE emails SET isArchived = :isArchived WHERE id = :id")
    suspend fun setArchived(id: String, isArchived: Boolean)

    @Query("UPDATE emails SET isDeleted = :isDeleted WHERE id = :id")
    suspend fun setDeleted(id: String, isDeleted: Boolean)

    @Query("UPDATE emails SET snoozeUntil = :snoozeUntil WHERE id = :id")
    suspend fun snoozeEmail(id: String, snoozeUntil: Long)

    @Query("UPDATE emails SET isSpam = :isSpam, isArchived = :isSpam WHERE id = :id")
    suspend fun setSpam(id: String, isSpam: Boolean)

    @Query("UPDATE emails SET isUnsubscribed = 1 WHERE mailingListSender = :senderEmail OR senderEmail = :senderEmail")
    suspend fun unsubscribeSender(senderEmail: String)

    @Query("DELETE FROM emails WHERE id = :id")
    suspend fun deletePermanently(id: String)

    @Query("DELETE FROM emails WHERE isDeleted = 1")
    suspend fun emptyTrash()

    @Query("SELECT COUNT(*) FROM emails WHERE isArchived = 0 AND isDeleted = 0 AND isSpam = 0 AND isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM emails WHERE isArchived = 0 AND isDeleted = 0 AND isSpam = 0 AND aiPriorityScore >= :minScore AND isRead = 0")
    fun getPriorityUnreadCount(minScore: Int = 70): Flow<Int>

    @Query("SELECT COUNT(*) FROM emails")
    suspend fun getTotalEmailCount(): Int
}
