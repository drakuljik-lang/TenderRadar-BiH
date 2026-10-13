package com.example

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.data.local.AppDatabase
import com.example.data.remote.TenderApiClient
import com.example.data.repository.DemoTenderRepository
import com.example.data.repository.TenderRepository
import com.example.data.repository.TenderSyncRepository
import com.example.notifications.TenderSyncWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class TenderRadarApplication : Application() {

    lateinit var repository: TenderRepository
        private set

    lateinit var database: AppDatabase
        private set

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(this)
        val local = DemoTenderRepository(database)
        repository = TenderSyncRepository(
            local = local,
            api = TenderApiClient.api,
            tenderDao = database.tenderDao()
        )

        // 1. Odmah obriši DEMO notifikacije
        appScope.launch {
            try {
                database.notificationDao().deleteAllDemo()
            } catch (_: Exception) {}
        }

        // 2. Odmah pokreni sync i generiši prave notifikacije
        WorkManager.getInstance(this).enqueue(
            OneTimeWorkRequestBuilder<TenderSyncWorker>().build()
        )

        // 3. Zakazi pozadinski sync svakih 6h
        val periodicRequest = PeriodicWorkRequestBuilder<TenderSyncWorker>(
            6, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "tender_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )
    }
}
