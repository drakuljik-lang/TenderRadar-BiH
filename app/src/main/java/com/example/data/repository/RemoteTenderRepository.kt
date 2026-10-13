package com.example.data.repository

import com.example.data.model.CompanyProfile
import com.example.data.model.NotificationItem
import com.example.data.model.Tender
import com.example.data.remote.TenderApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * RemoteTenderRepository - Pripremljena implementacija za spajanje na produkcijski
 * TenderRadar REST API kada backend server bude pušten u rad.
 * Trenutno aplikacija primarno koristi DemoTenderRepository sa Room bazom podataka.
 */
class RemoteTenderRepository(
    @Suppress("unused") private val api: TenderApi
) : TenderRepository {

    override fun getAllTenders(): Flow<List<Tender>> = flow {
        // Kada API bude operativan: emit(api.getTenders().body() ?: emptyList())
        throw UnsupportedOperationException("Produkcijski API server je u fazi povezivanja. Koristi se lokalni DemoTenderRepository.")
    }

    override suspend fun getTenderById(id: String): Tender? {
        throw UnsupportedOperationException("Produkcijski API server je u fazi povezivanja.")
    }

    override suspend fun refreshTenders(): Result<Unit> {
        return Result.failure(UnsupportedOperationException("Produkcijski API server je u pripremi."))
    }

    override fun getCompanyProfile(): Flow<CompanyProfile> = flow {
        throw UnsupportedOperationException("Produkcijski API profil u pripremi.")
    }

    override suspend fun updateCompanyProfile(profile: CompanyProfile) {
        throw UnsupportedOperationException("Produkcijski API profil u pripremi.")
    }

    override fun getSavedTenderIds(): Flow<Set<String>> = flow {
        emit(emptySet())
    }

    override fun isTenderSaved(tenderId: String): Flow<Boolean> = flow {
        emit(false)
    }

    override suspend fun toggleSaveTender(tenderId: String) {}

    override fun getNotifications(): Flow<List<NotificationItem>> = flow {
        emit(emptyList())
    }

    override suspend fun markNotificationAsRead(id: String) {}
    override suspend fun markAllNotificationsAsRead() {}
    override suspend fun addNotification(notification: NotificationItem) {}
}
