package com.example

import com.example.data.datasource.DemoData
import com.example.data.model.CompanyProfile
import com.example.data.model.MatchLevel
import com.example.data.model.Tender
import com.example.domain.matcher.MatchScoreCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MatchScoreCalculatorTest {

    private val baseProfile = CompanyProfile(
        companyName = "ABC Elektro d.o.o.",
        activity = "Elektroinstalacije",
        city = "Zenica",
        canton = "ZDK",
        keywords = listOf("elektroinstalacije", "elektromaterijal", "rasvjeta"),
        cpvCategories = listOf("45310000", "45311000"),
        minBudgetKm = 20000.0,
        maxBudgetKm = 300000.0
    )

    private val dummyTender = Tender(
        id = "T-TEST",
        title = "Ispitivanje elektroinstalacija i rasvjete",
        institution = "Općina Zenica",
        city = "Zenica",
        canton = "ZDK",
        entity = "Federacija BiH",
        estimatedValueKm = 50000.0,
        publishedDate = "01.10.2026.",
        deadlineDate = "15.10.2026.",
        daysUntilDeadline = 14,
        category = "Elektroinstalacije",
        cpvCode = "45311000-0",
        cpvDescription = "Elektroinstalacijski radovi",
        description = "Radovi na zamjeni rasvjete i rekonstrukciji elektroinstalacija."
    )

    @Test
    fun testFullMatch() {
        // All 5 factors match: keywords, CPV, category, location, budget
        val result = MatchScoreCalculator.calculateMatch(dummyTender, baseProfile)
        assertTrue("Score should be >= 80 for full match, was: ${result.score}", result.score >= 80)
        assertEquals(MatchLevel.HIGH, result.level)
        assertTrue(result.reasons.isNotEmpty())
    }

    @Test
    fun testNoMatch() {
        val nonMatchingTender = dummyTender.copy(
            title = "Nabavka svježeg mesa i mesnih prerađevina",
            institution = "Bolnica Trebinje",
            city = "Trebinje",
            canton = "Hercegovina",
            entity = "Republika Srpska",
            estimatedValueKm = 1_500_000.0, // outside budget
            category = "Hrana",
            cpvCode = "15110000-2",
            cpvDescription = "Meso",
            description = "Isporuka goveđeg i pilećeg mesa."
        )

        val result = MatchScoreCalculator.calculateMatch(nonMatchingTender, baseProfile)
        assertTrue("Score should be < 40 for complete non-match, was: ${result.score}", result.score < 40)
        assertEquals(MatchLevel.LOW, result.level)
    }

    @Test
    fun testKeywordMatch() {
        val tenderWithKeywordsOnly = dummyTender.copy(
            city = "Bihać",
            canton = "USK",
            estimatedValueKm = 2_000_000.0,
            category = "Razno",
            cpvCode = "99999999-9"
        )

        val result = MatchScoreCalculator.calculateMatch(tenderWithKeywordsOnly, baseProfile)
        assertTrue("Keywords should award points (keywordScore > 0)", result.keywordScore > 0)
        assertTrue(result.reasons.any { it.contains("Ključne riječi") })
    }

    @Test
    fun testCpvMatch() {
        val tenderWithCpvOnly = dummyTender.copy(
            title = "Nabavka opreme X",
            description = "Bez specifičnih ključnih riječi",
            city = "Banja Luka",
            canton = "Regija BL",
            estimatedValueKm = 2_000_000.0,
            category = "Ostalo",
            cpvCode = "45311000-0"
        )

        val result = MatchScoreCalculator.calculateMatch(tenderWithCpvOnly, baseProfile)
        assertEquals("CPV match should award 25 points", 25, result.cpvScore)
        assertTrue(result.reasons.any { it.contains("CPV") })
    }

    @Test
    fun testLocationMatch() {
        val tenderWithLocationOnly = dummyTender.copy(
            title = "Nabavka sanitarnog materijala",
            description = "Bez elektro materijala",
            city = "Zenica",
            canton = "ZDK",
            estimatedValueKm = 2_000_000.0,
            category = "Sanitarije",
            cpvCode = "33140000-3"
        )

        val result = MatchScoreCalculator.calculateMatch(tenderWithLocationOnly, baseProfile)
        assertEquals("Exact city match should award 10 points", 10, result.locationScore)
        assertTrue(result.reasons.any { it.contains("Lokalni tender: Zenica") })
    }

    @Test
    fun testCategoryMatch() {
        val tenderWithCategoryOnly = dummyTender.copy(
            title = "Opći radovi",
            description = "Nema detalja",
            city = "Mostar",
            canton = "HNK",
            estimatedValueKm = 2_000_000.0,
            category = "Elektroinstalacije",
            cpvCode = "99999999-9"
        )

        val result = MatchScoreCalculator.calculateMatch(tenderWithCategoryOnly, baseProfile)
        assertEquals("Category match should award 15 points", 15, result.categoryScore)
        assertTrue(result.reasons.any { it.contains("kategorija djelatnosti") })
    }

    @Test
    fun testDeadlineHelperAndFallbacks() {
        val activeTender = dummyTender.copy(daysUntilDeadline = 2)
        val infoActive = com.example.domain.util.DeadlineHelper.parseDeadline(activeTender)
        assertTrue(infoActive.isUrgent)
        assertEquals(com.example.domain.util.UrgencyLevel.CRITICAL, infoActive.urgencyLevel)
        assertEquals("JOŠ 2 D.", infoActive.badgeLabel)

        val expiredTender = dummyTender.copy(daysUntilDeadline = -1, status = "Završen")
        val infoExpired = com.example.domain.util.DeadlineHelper.parseDeadline(expiredTender)
        assertTrue(infoExpired.isExpired)
        assertEquals("ROK ISTEKAO", infoExpired.statusText)

        val missingDateTender = dummyTender.copy(deadlineDate = "")
        val infoMissing = com.example.domain.util.DeadlineHelper.parseDeadline(missingDateTender)
        assertEquals(com.example.domain.util.UrgencyLevel.UNKNOWN, infoMissing.urgencyLevel)

        val valueFormatted = com.example.domain.util.DeadlineHelper.formatEstimatedValue(50000.0)
        assertTrue(valueFormatted.contains("50.000 KM"))

        val zeroValueFormatted = com.example.domain.util.DeadlineHelper.formatEstimatedValue(0.0)
        assertEquals("Podatak nije dostupan.", zeroValueFormatted)
    }
}
