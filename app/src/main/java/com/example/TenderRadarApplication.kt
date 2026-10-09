package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.data.remote.TenderApiClient
import com.example.data.repository.DemoTenderRepository
import com.example.data.repository.TenderRepository
import com.example.data.repository.TenderSyncRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

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

        // Automatski sync sa e-Nabavke API-ja pri pokretanju aplikacije
        appScope.launch {
            try {
                repository.refreshTenders()
            } catch (e: Exception) {
                // Tiho ignorisati - demo podaci ostaju kao fallback
                android.util.Log.w("TenderRadar", "Sync failed: ${e.message}")
            }
        }
    }
}
