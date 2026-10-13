package com.example.data.repository

import com.example.data.datasource.DemoData
import com.example.data.local.AppDatabase
import com.example.data.local.CompanyProfileEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.SavedTenderEntity
import com.example.data.local.TenderEntity
import com.example.data.model.CompanyProfile
import com.example.data.model.NotificationItem
import com.example.data.model.Tender
import com.example.data.model.TenderDocument
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class DemoTenderRepository(
    private val database: AppDatabase,
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : TenderRepository {

    private val tenderDao = database.tenderDao()
    private val profileDao = database.companyProfileDao()
    private val savedDao = database.savedTenderDao()
    private val notificationDao = database.notificationDao()

    init {
        externalScope.launch {
            seedInitialDataIfNeeded()
        }
    }

    private suspend fun seedInitialDataIfNeeded() = withContext(Dispatchers.IO) {
        val existingTenders = tenderDao.getAllTenders().first()
        if (existingTenders.isEmpty()) {
            val entities = DemoData.demoTenders.map { it.toEntity() }
            tenderDao.insertAll(entities)
        }

        val existingProfile = profileDao.getProfile()
        if (existingProfile == null) {
            profileDao.saveProfile(DemoData.defaultCompanyProfile.toEntity())
        }

        // NAPOMENA: DEMO notifikacije se NE seed-uju automatski.
        // Prave notifikacije generiše TenderSyncWorker iz stvarnih tendera.
        // Ako želiš DEMO notifikacije za test, uključi ih ručno preko
        // DemoData.sampleNotifications u test okruženju.
    }

    override fun getAllTenders(): Flow<List<Tender>> {
        return tenderDao.getAllTenders().map { entities ->
            if (entities.isEmpty()) {
                // Fallback to static demo data while Room seeds
                DemoData.demoTenders
            } else {
                entities.map { it.toDomain() }
            }
        }
    }

    override suspend fun getTenderById(id: String): Tender? = withContext(Dispatchers.IO) {
        tenderDao.getTenderById(id)?.toDomain() ?: DemoData.demoTenders.find { it.id == id }
    }

    override suspend fun refreshTenders(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // U offline/demo modu osvježavamo lokalne demo podatke
            val entities = DemoData.demoTenders.map { it.toEntity() }
            tenderDao.insertAll(entities)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCompanyProfile(): Flow<CompanyProfile> {
        return profileDao.getProfileFlow().map { entity ->
            entity?.toDomain() ?: DemoData.defaultCompanyProfile
        }
    }

    override suspend fun updateCompanyProfile(profile: CompanyProfile) = withContext(Dispatchers.IO) {
        profileDao.saveProfile(profile.toEntity())
    }

    override fun getSavedTenderIds(): Flow<Set<String>> {
        return savedDao.getAllSaved().map { list -> list.map { it.tenderId }.toSet() }
    }

    override fun isTenderSaved(tenderId: String): Flow<Boolean> {
        return savedDao.isSavedFlow(tenderId)
    }

    override suspend fun toggleSaveTender(tenderId: String) = withContext(Dispatchers.IO) {
        val isSaved = savedDao.isSaved(tenderId)
        if (isSaved) {
            savedDao.delete(tenderId)
        } else {
            savedDao.save(SavedTenderEntity(tenderId = tenderId))
        }
    }

    override fun getNotifications(): Flow<List<NotificationItem>> {
        return notificationDao.getAllNotifications().map { entities ->
            if (entities.isEmpty()) {
                DemoData.sampleNotifications
            } else {
                entities.map { it.toDomain() }
            }
        }
    }

    override suspend fun markNotificationAsRead(id: String) = withContext(Dispatchers.IO) {
        notificationDao.markAsRead(id)
    }

    override suspend fun markAllNotificationsAsRead() = withContext(Dispatchers.IO) {
        notificationDao.markAllAsRead()
    }

    override suspend fun addNotification(notification: NotificationItem) = withContext(Dispatchers.IO) {
        notificationDao.insert(notification.toEntity())
    }

    // Mapping extensions
    private fun Tender.toEntity(): TenderEntity {
        val docsArray = JSONArray()
        documents.forEach { doc ->
            val obj = JSONObject().apply {
                put("id", doc.id)
                put("title", doc.title)
                put("type", doc.type)
                put("fileFormat", doc.fileFormat)
                put("sizeText", doc.sizeText)
                put("downloadUrl", doc.downloadUrl)
            }
            docsArray.put(obj)
        }
        return TenderEntity(
            id = id,
            title = title,
            institution = institution,
            city = city,
            canton = canton,
            entity = entity,
            estimatedValueKm = estimatedValueKm,
            publishedDate = publishedDate,
            deadlineDate = deadlineDate,
            daysUntilDeadline = daysUntilDeadline,
            category = category,
            cpvCode = cpvCode,
            cpvDescription = cpvDescription,
            description = description,
            documentsJson = docsArray.toString(),
            sourceUrl = sourceUrl,
            isDemo = isDemo,
            status = status
        )
    }

    private fun TenderEntity.toDomain(): Tender {
        val docList = mutableListOf<TenderDocument>()
        if (documentsJson.isNotBlank()) {
            try {
                val array = JSONArray(documentsJson)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    docList.add(
                        TenderDocument(
                            id = obj.optString("id"),
                            title = obj.optString("title"),
                            type = obj.optString("type"),
                            fileFormat = obj.optString("fileFormat"),
                            sizeText = obj.optString("sizeText"),
                            downloadUrl = obj.optString("downloadUrl")
                        )
                    )
                }
            } catch (_: Exception) {
            }
        }
        return Tender(
            id = id,
            title = title,
            institution = institution,
            city = city,
            canton = canton,
            entity = entity,
            estimatedValueKm = estimatedValueKm,
            publishedDate = publishedDate,
            deadlineDate = deadlineDate,
            daysUntilDeadline = daysUntilDeadline,
            category = category,
            cpvCode = cpvCode,
            cpvDescription = cpvDescription,
            description = description,
            documents = docList,
            sourceUrl = sourceUrl,
            isDemo = isDemo,
            status = status
        )
    }

    private fun CompanyProfile.toEntity(): CompanyProfileEntity {
        return CompanyProfileEntity(
            id = id,
            companyName = companyName,
            activity = activity,
            city = city,
            canton = canton,
            keywordsCsv = keywords.joinToString(";"),
            cpvCategoriesCsv = cpvCategories.joinToString(";"),
            minBudgetKm = minBudgetKm,
            maxBudgetKm = maxBudgetKm,
            isConfigured = isConfigured
        )
    }

    private fun CompanyProfileEntity.toDomain(): CompanyProfile {
        return CompanyProfile(
            id = id,
            companyName = companyName,
            activity = activity,
            city = city,
            canton = canton,
            keywords = keywordsCsv.split(";").filter { it.isNotBlank() },
            cpvCategories = cpvCategoriesCsv.split(";").filter { it.isNotBlank() },
            minBudgetKm = minBudgetKm,
            maxBudgetKm = maxBudgetKm,
            isConfigured = isConfigured
        )
    }

    private fun NotificationItem.toEntity(): NotificationEntity {
        return NotificationEntity(
            id = id,
            title = title,
            message = message,
            tenderId = tenderId,
            timestamp = timestamp,
            type = type,
            isRead = isRead
        )
    }

    private fun NotificationEntity.toDomain(): NotificationItem {
        return NotificationItem(
            id = id,
            title = title,
            message = message,
            tenderId = tenderId,
            timestamp = timestamp,
            type = type,
            isRead = isRead
        )
    }
}
