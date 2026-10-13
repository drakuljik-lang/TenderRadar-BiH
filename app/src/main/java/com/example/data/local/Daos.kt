package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TenderDao {
    @Query("SELECT * FROM tenders ORDER BY deadlineDate ASC")
    fun getAllTenders(): Flow<List<TenderEntity>>

    @Query("SELECT * FROM tenders WHERE id = :id LIMIT 1")
    suspend fun getTenderById(id: String): TenderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tenders: List<TenderEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tender: TenderEntity)

    @Query("DELETE FROM tenders WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM tenders")
    suspend fun clearAll()
}

@Dao
interface CompanyProfileDao {
    @Query("SELECT * FROM company_profile WHERE id = :id LIMIT 1")
    fun getProfileFlow(id: String = "default_company_profile"): Flow<CompanyProfileEntity?>

    @Query("SELECT * FROM company_profile WHERE id = :id LIMIT 1")
    suspend fun getProfile(id: String = "default_company_profile"): CompanyProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: CompanyProfileEntity)
}

@Dao
interface SavedTenderDao {
    @Query("SELECT * FROM saved_tenders ORDER BY savedAt DESC")
    fun getAllSaved(): Flow<List<SavedTenderEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_tenders WHERE tenderId = :tenderId)")
    fun isSavedFlow(tenderId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_tenders WHERE tenderId = :tenderId)")
    suspend fun isSaved(tenderId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(savedTender: SavedTenderEntity)

    @Query("DELETE FROM saved_tenders WHERE tenderId = :tenderId")
    suspend fun delete(tenderId: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun delete(id: String)
}
