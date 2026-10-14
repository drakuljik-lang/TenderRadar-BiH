package com.example.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.local.AppDatabase
import com.example.data.model.NotificationItem
import com.example.data.repository.TenderRepository
import kotlinx.coroutines.flow.first

class TenderSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val app = applicationContext as? com.example.TenderRadarApplication
            ?: return Result.failure()

        val repository: TenderRepository = app.repository
        val database: AppDatabase = app.database

        return try {
            // 1. Sinhronizacija mora uspjeti prije daljnje obrade.
            val syncResult = repository.refreshTenders()
            if (syncResult.isFailure) {
                return Result.retry()
            }

            // 2. Brisi demo notifikacije tek nakon uspjesne sinhronizacije.
            database.notificationDao().deleteAllDemo()

            // 3. Generiši prave notifikacije iz tendera
            val tenders = repository.getAllTenders().first()
            val profile = repository.getCompanyProfile().first()

            val newNotifications = mutableListOf<NotificationItem>()
            var counter = 0

            for (tender in tenders) {
                if (counter >= 20) break // max 20 notifikacija po syncu

                // Preskoci demo tendere
                if (tender.isDemo) continue

                // Upozorenje za rok < 3 dana
                if (tender.daysUntilDeadline in 0..3) {
                    newNotifications += NotificationItem(
                        id = "sync-deadline-${tender.id}",
                        title = "Rok ističe za ${tender.daysUntilDeadline} dana",
                        message = "${tender.institution}: ${tender.title.take(80)}",
                        tenderId = tender.id,
                        type = "DEADLINE_SOON",
                        isRead = false
                    )
                    counter++
                }
            }

            // 4. Sačuvaj nove notifikacije u bazu
            if (newNotifications.isNotEmpty()) {
                val entities = newNotifications.map { it.toEntity() }
                database.notificationDao().insertAll(entities)

                // 5. Prikaži sistemsku notifikaciju za prvu
                NotificationHelper.showSystemNotification(
                    applicationContext,
                    newNotifications.first()
                )
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

// Helper — NotificationItem u NotificationEntity
private fun NotificationItem.toEntity() =
    com.example.data.local.NotificationEntity(
        id = this.id,
        title = this.title,
        message = this.message,
        tenderId = this.tenderId,
        timestamp = this.timestamp,
        type = this.type,
        isRead = this.isRead
    )
