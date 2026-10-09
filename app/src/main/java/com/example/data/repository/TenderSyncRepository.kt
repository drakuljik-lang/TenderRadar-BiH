package com.example.data.repository

import com.example.data.local.TenderDao
import com.example.data.remote.TenderApi
import com.example.data.remote.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class TenderSyncRepository(
    private val local: TenderRepository,
    private val api: TenderApi,
    private val tenderDao: TenderDao
) : TenderRepository by local {

    override suspend fun refreshTenders(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.getProcurementNotices(
                top = 100,
                orderBy = "Announced desc"
            )
            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    IOException("e-Nabavke API vratio HTTP ${response.code()}")
                )
            }
            val dtos = response.body()?.value.orEmpty()
            val entities = dtos.map { it.toEntity() }
            tenderDao.insertAll(entities)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
