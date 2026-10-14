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

    override suspend fun refreshTenders(): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val allEntities = mutableListOf<com.example.data.local.TenderEntity>()

                for (page in 0 until 10) {
                    val skip = page * 50

                    val response = api.getProcurementNotices(
                        top = 50,
                        skip = skip,
                        orderBy = "Announced desc"
                    )

                    if (!response.isSuccessful) {
                        return@withContext Result.failure(
                            IOException(
                                "e-Nabavke API HTTP ${response.code()}, skip=$skip"
                            )
                        )
                    }

                    val body = response.body()
                        ?: return@withContext Result.failure(
                            IOException("e-Nabavke API je vratio prazan/neispravan odgovor.")
                        )

                    val dtos = body.value

                    if (dtos.isEmpty()) {
                        break
                    }

                    allEntities.addAll(dtos.map { it.toEntity() })

                    if (dtos.size < 50) {
                        break
                    }

                    if (page < 9) {
                        delay(300)
                    }
                }

                if (allEntities.isEmpty()) {
                    return@withContext Result.failure(
                        IOException(
                            "API nije vratio nijedan tender. Postojeci podaci nisu obrisani."
                        )
                    )
                }

                // Prvo upisi stvarne tendere. Demo ukloni tek nakon uspjesnog upisa.
                tenderDao.insertAll(allEntities)
                tenderDao.deleteDemoTenders()

                Result.success(Unit)
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
