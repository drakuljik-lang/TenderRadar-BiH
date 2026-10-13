package com.example.domain.matcher

import com.example.data.model.CompanyProfile
import com.example.data.model.MatchLevel
import com.example.data.model.Tender
import com.example.data.model.TenderMatchExplanation
import java.util.Locale
import kotlin.math.roundToInt

object MatchScoreCalculator {

    /**
     * Izračunava Match Score (0 - 100) na osnovu 5 ponderisanih faktora:
     * - Keyword match: max 30%
     * - CPV match: max 25%
     * - Category match: max 15%
     * - Location match: max 10%
     * - Company profile budget match: max 20%
     */
    fun calculateMatch(tender: Tender, profile: CompanyProfile): TenderMatchExplanation {
        val reasons = mutableListOf<String>()

        // 1. KEYWORD MATCH (max 30 bodova)
        val tenderSearchText = "${tender.title} ${tender.description} ${tender.cpvDescription}".lowercase(Locale.ROOT)
        var keywordPoints = 0
        val matchedKeywords = mutableListOf<String>()

        if (profile.keywords.isNotEmpty()) {
            profile.keywords.forEach { kw ->
                val cleanKw = kw.trim().lowercase(Locale.ROOT)
                if (cleanKw.isNotBlank()) {
                    val stem1 = if (cleanKw.length >= 5) cleanKw.dropLast(1) else cleanKw
                    val stem2 = if (cleanKw.length >= 6) cleanKw.dropLast(2) else stem1
                    if (tenderSearchText.contains(cleanKw) || tenderSearchText.contains(stem1) || tenderSearchText.contains(stem2)) {
                        matchedKeywords.add(kw.trim())
                    }
                }
            }
            if (matchedKeywords.isNotEmpty()) {
                val ratio = (matchedKeywords.size.toDouble() / profile.keywords.size.toDouble()).coerceAtMost(1.0)
                // Osnovni bonus za pogodak (najmanje 15 bodova), skalirano do 30
                keywordPoints = (15 + (ratio * 15)).roundToInt().coerceIn(15, 30)
                reasons.add("Ključne riječi: ${matchedKeywords.take(3).joinToString(", ")}")
            }
        }

        // 2. CPV MATCH (max 25 bodova)
        var cpvPoints = 0
        val tenderCpvClean = tender.cpvCode.replace("-", "").trim()
        val hasCpvMatch = profile.cpvCategories.any { userCpv ->
            val userClean = userCpv.replace("-", "").trim()
            if (userClean.length >= 4 && tenderCpvClean.length >= 4) {
                // Provjera po prva 4 ili 5 karaktera (CPV grupa i razred)
                tenderCpvClean.startsWith(userClean.take(4)) || userClean.startsWith(tenderCpvClean.take(4))
            } else {
                tenderCpvClean.contains(userClean) || userClean.contains(tenderCpvClean)
            }
        }

        if (hasCpvMatch) {
            cpvPoints = 25
            reasons.add("Odgovarajući CPV kod (${tender.cpvCode})")
        } else if (profile.cpvCategories.any { tenderCpvClean.take(2) == it.replace("-", "").take(2) }) {
            // Ista šira CPV divizija (prve 2 cifre)
            cpvPoints = 12
            reasons.add("Srodna CPV djelatnost (${tender.cpvCode.take(2)}xxxx)")
        }

        // 3. CATEGORY MATCH (max 15 bodova)
        var categoryPoints = 0
        val normTenderCat = tender.category.lowercase(Locale.ROOT)
        val normActivity = profile.activity.lowercase(Locale.ROOT)
        if (normTenderCat.contains("elektro") && normActivity.contains("elektro") ||
            normTenderCat.contains("građev") && normActivity.contains("građev") ||
            normTenderCat.contains("it") && normActivity.contains("it") ||
            normTenderCat.contains("medic") && normActivity.contains("medic") ||
            normTenderCat.contains("transport") && normActivity.contains("transport") ||
            normTenderCat.contains("održavanj") && normActivity.contains("održavanj") ||
            normTenderCat == normActivity
        ) {
            categoryPoints = 15
            reasons.add("Vaša kategorija djelatnosti (${tender.category})")
        } else if (normActivity.split(" ", "/", ",").any { it.length > 4 && normTenderCat.contains(it) }) {
            categoryPoints = 10
            reasons.add("Djelimično podudaranje kategorije (${tender.category})")
        }

        // 4. LOCATION MATCH (max 10 bodova)
        var locationPoints = 0
        val normCity = profile.city.lowercase(Locale.ROOT).trim()
        val normCanton = profile.canton.lowercase(Locale.ROOT).trim()
        val tenderCity = tender.city.lowercase(Locale.ROOT).trim()
        val tenderCanton = tender.canton.lowercase(Locale.ROOT).trim()

        if (normCity.isNotBlank() && (tenderCity.contains(normCity) || normCity.contains(tenderCity))) {
            locationPoints = 10
            reasons.add("Lokalni tender: ${tender.city} (${tender.canton})")
        } else if (normCanton.isNotBlank() && (tenderCanton.contains(normCanton) || normCanton.contains(tenderCanton))) {
            locationPoints = 8
            reasons.add("Područje vašeg kantona: ${tender.canton}")
        } else if (tender.entity.isNotBlank()) {
            locationPoints = 4
        }

        // 5. BUDGET MATCH (max 20 bodova)
        var budgetPoints = 0
        val value = tender.estimatedValueKm
        if (value in profile.minBudgetKm..profile.maxBudgetKm) {
            budgetPoints = 20
            reasons.add("Vrijednost u vašem rasponu (${formatKm(value)})")
        } else if (value >= profile.minBudgetKm * 0.75 && value <= profile.maxBudgetKm * 1.3) {
            budgetPoints = 10
            reasons.add("Vrijednost blizu vašeg raspona (${formatKm(value)})")
        }

        val totalScore = (keywordPoints + cpvPoints + categoryPoints + locationPoints + budgetPoints).coerceIn(0, 100)

        val level = when (totalScore) {
            in 80..100 -> MatchLevel.HIGH
            in 60..79 -> MatchLevel.GOOD
            in 40..59 -> MatchLevel.POSSIBLE
            else -> MatchLevel.LOW
        }

        if (reasons.isEmpty()) {
            reasons.add("Tender ne odgovara direktno parametrima profila firme")
        }

        return TenderMatchExplanation(
            score = totalScore,
            level = level,
            reasons = reasons,
            keywordScore = keywordPoints,
            cpvScore = cpvPoints,
            categoryScore = categoryPoints,
            locationScore = locationPoints,
            budgetScore = budgetPoints
        )
    }

    private fun formatKm(value: Double): String {
        return if (value >= 1_000_000) {
            String.format(Locale.GERMAN, "%.2f mil. KM", value / 1_000_000.0)
        } else {
            String.format(Locale.GERMAN, "%,.0f KM", value)
        }
    }
}
