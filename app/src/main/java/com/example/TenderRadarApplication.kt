package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.data.repository.DemoTenderRepository
import com.example.data.repository.TenderRepository

class TenderRadarApplication : Application() {

    lateinit var repository: TenderRepository
        private set

    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(this)
        repository = DemoTenderRepository(database)
    }
}
