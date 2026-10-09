package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tenders")
data class TenderEntity(
    @PrimaryKey val id: String,
    val title: String,
    val institution: String,
    val city: String,
    val canton: String,
    val entity: String,
    val estimatedValueKm: Double,
    val publishedDate: String,
    val deadlineDate: String,
    val daysUntilDeadline: Int,
    val category: String,
    val cpvCode: String,
    val cpvDescription: String,
    val description: String,
    val documentsJson: String,
    val sourceUrl: String,
    val isDemo: Boolean = true,
    val status: String = "Aktivan"
)

@Entity(tableName = "company_profile")
data class CompanyProfileEntity(
    @PrimaryKey val id: String = "default_company_profile",
    val companyName: String,
    val activity: String,
    val city: String,
    val canton: String,
    val keywordsCsv: String,
    val cpvCategoriesCsv: String,
    val minBudgetKm: Double,
    val maxBudgetKm: Double,
    val isConfigured: Boolean
)

@Entity(tableName = "saved_tenders")
data class SavedTenderEntity(
    @PrimaryKey val tenderId: String,
    val savedAt: Long = System.currentTimeMillis(),
    val notes: String = "",
    val reminderSet: Boolean = false
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val tenderId: String?,
    val timestamp: Long,
    val type: String,
    val isRead: Boolean = false
)
