package com.example.data.repository

import com.example.data.local.TenderDao
import com.example.data.remote.TenderApi
import com.example.data.remote.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException

class TenderSyncRepository(
    private val local: TenderRepository,
    private val api: TenderApi,
    private val tenderDao: TenderDao
) : TenderRepository by local {

    override suspend fun refreshTenders(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            for (page in 0 until 10) {
                val currentSkip = page * 50
                val response = api.getProcurementNotices(
                    top = 50,
                    skip = currentSkip,
                    orderBy = "Announced desc"
                )

                if (!response.isSuccessful) {
                    return@withContext Result.failure(
                        IOException("e-Nabavke API vratio HTTP ${response.code()} na skip=$currentSkip")
                    )
                }

                val dtos = response.body()?.value.orEmpty()
                if (dtos.isEmpty()) {
                    break
                }

                val entities = dtos.map { it.toEntity() }
                tenderDao.insertAll(entities)

                if (dtos.size < 50) {
                    break
                }

                if (page < 9) {
                    delay(300)
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
