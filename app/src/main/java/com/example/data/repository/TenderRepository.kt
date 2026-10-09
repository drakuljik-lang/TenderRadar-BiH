package com.example.data.repository

import com.example.data.model.CompanyProfile
import com.example.data.model.NotificationItem
import com.example.data.model.Tender
import kotlinx.coroutines.flow.Flow

interface TenderRepository {
    fun getAllTenders(): Flow<List<Tender>>
    suspend fun getTenderById(id: String): Tender?
    suspend fun refreshTenders(): Result<Unit>

    fun getCompanyProfile(): Flow<CompanyProfile>
    suspend fun updateCompanyProfile(profile: CompanyProfile)

    fun getSavedTenderIds(): Flow<Set<String>>
    fun isTenderSaved(tenderId: String): Flow<Boolean>
    suspend fun toggleSaveTender(tenderId: String)

    fun getNotifications(): Flow<List<NotificationItem>>
    suspend fun markNotificationAsRead(id: String)
    suspend fun markAllNotificationsAsRead()
    suspend fun addNotification(notification: NotificationItem)
}
