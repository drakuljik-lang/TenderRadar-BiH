package com.example.data.model

import java.io.Serializable

enum class MatchLevel(val label: String) {
    HIGH("VISOKA PODUDARNOST"),
    GOOD("DOBRA PODUDARNOST"),
    POSSIBLE("MOGUĆA PODUDARNOST"),
    LOW("NISKA PODUDARNOST")
}

data class TenderMatchExplanation(
    val score: Int,
    val level: MatchLevel,
    val reasons: List<String>,
    val keywordScore: Int,
    val cpvScore: Int,
    val categoryScore: Int,
    val locationScore: Int,
    val budgetScore: Int
)

data class TenderDocument(
    val id: String,
    val title: String,
    val type: String, // "Tenderska dokumentacija", "Obrazac", "Tehnička specifikacija"
    val fileFormat: String, // PDF, DOCX
    val sizeText: String,
    val downloadUrl: String
) : Serializable

data class Tender(
    val id: String,
    val title: String,
    val institution: String,
    val city: String,
    val canton: String, // npr. "Zeničko-dobojski (ZDK)", "Kanton Sarajevo (KS)", "Tuzlanski (TK)"
    val entity: String, // "Federacija BiH", "Republika Srpska", "Brčko distrikt"
    val estimatedValueKm: Double,
    val publishedDate: String, // "02.10.2026."
    val deadlineDate: String, // "18.10.2026."
    val daysUntilDeadline: Int,
    val category: String, // "Elektroinstalacije", "Građevina", "IT oprema i softver", itd.
    val cpvCode: String, // npr. "45311000-0"
    val cpvDescription: String,
    val description: String,
    val documents: List<TenderDocument> = emptyList(),
    val sourceUrl: String = "https://ejn.gov.ba/tender-demo",
    val isDemo: Boolean = true,
    val status: String = "Aktivan" // "Aktivan", "U toku", "Završen"
) : Serializable

data class CompanyProfile(
    val id: String = "default_company_profile",
    val companyName: String = "ABC Elektro d.o.o.",
    val activity: String = "Elektroinstalacijski radovi i inženjering",
    val city: String = "Zenica",
    val canton: String = "ZDK",
    val keywords: List<String> = listOf("elektroinstalacije", "elektromaterijal", "rasvjeta", "električni radovi"),
    val cpvCategories: List<String> = listOf("45310000", "45311000", "31682000", "31527200"),
    val minBudgetKm: Double = 20000.0,
    val maxBudgetKm: Double = 500000.0,
    val isConfigured: Boolean = true
) : Serializable

data class SavedTender(
    val tenderId: String,
    val savedAt: Long = System.currentTimeMillis(),
    val notes: String = "",
    val reminderSet: Boolean = false
)

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val tenderId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String, // "MATCH_HIGH", "DEADLINE_SOON", "NEW_TENDER"
    val isRead: Boolean = false
)

data class TenderFilter(
    val query: String = "",
    val city: String = "",
    val canton: String = "",
    val entity: String = "",
    val category: String = "",
    val cpv: String = "",
    val minBudget: Double? = null,
    val maxBudget: Double? = null,
    val minMatchScore: Int? = null,
    val onlyMatches: Boolean = false,
    val onlyUrgentDeadline: Boolean = false, // < 7 dana
    val onlyHighValue: Boolean = false, // > 100.000 KM
    val onlyZenicaZdk: Boolean = false
)

data class AiSummaryData(
    val tenderId: String,
    val whatIsProcured: String,
    val whoCanApply: String,
    val estimatedValue: String,
    val deadline: String,
    val keyDocuments: List<String>,
    val criticalCaveats: List<String>
)
