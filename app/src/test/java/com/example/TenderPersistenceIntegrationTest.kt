package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.datasource.DemoData
import com.example.data.local.AppDatabase
import com.example.data.model.CompanyProfile
import com.example.data.model.MatchLevel
import com.example.data.repository.DemoTenderRepository
import com.example.domain.matcher.MatchScoreCalculator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TenderPersistenceIntegrationTest {

    private lateinit var database: AppDatabase
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testCompleteFlow_CreateProfile_SaveProfile_CalculateMatch_OpenTender_SaveTender_Restart_Verify() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val testScope = TestScope(testDispatcher)

        // 1. Inicijalizacija repozitorija
        var repository = DemoTenderRepository(database, testScope)

        // 2. Kreiranje i spremanje profila (Create profile & Save profile)
        val customProfile = CompanyProfile(
            id = "default_company_profile",
            companyName = "Zenica Elektro Inženjering d.o.o.",
            activity = "Elektroinstalacije",
            city = "Zenica",
            canton = "Zeničko-dobojski (ZDK)",
            keywords = listOf("elektroinstalacije", "rasvjeta", "kablova"),
            cpvCategories = listOf("45311000", "45316110"),
            minBudgetKm = 50000.0,
            maxBudgetKm = 400000.0,
            isConfigured = true
        )

        repository.updateCompanyProfile(customProfile)
        val savedProfile = repository.getCompanyProfile().first()
        assertEquals("Zenica Elektro Inženjering d.o.o.", savedProfile.companyName)
        assertEquals("Zenica", savedProfile.city)

        // 3. Otvaranje tendera (Open tender)
        // Prvo osiguraj da su seed tenderi ubačeni
        val allTenders = repository.getAllTenders().first()
        assertTrue("Baza mora sadržavati tendere", allTenders.isNotEmpty())

        val targetTender = repository.getTenderById("TND-BA-2026-001")
        assertNotNull("Tender TND-BA-2026-001 mora postojati", targetTender)

        // 4. Izračunavanje podudarnosti (Calculate match)
        val matchResult = MatchScoreCalculator.calculateMatch(targetTender!!, savedProfile)
        assertTrue("Match score za elektro tender i elektro firmu u Zenici mora biti visoka", matchResult.score >= 80)
        assertEquals(MatchLevel.HIGH, matchResult.level)
        assertTrue(matchResult.reasons.any { it.contains("Zenica") })

        // 5. Spremanje tendera (Save tender)
        val initialSavedIds = repository.getSavedTenderIds().first()
        assertTrue("Početno tender nije spremljen", !initialSavedIds.contains("TND-BA-2026-001"))

        repository.toggleSaveTender("TND-BA-2026-001")

        val savedIdsAfterToggle = repository.getSavedTenderIds().first()
        assertTrue("Tender mora biti u setu spremljenih nakon akcije", savedIdsAfterToggle.contains("TND-BA-2026-001"))

        val isSavedCheck = repository.isTenderSaved("TND-BA-2026-001").first()
        assertTrue("isTenderSaved mora vratiti true", isSavedCheck)

        // 6. Simulacija ponovnog pokretanja aplikacije (Restart app)
        // Kreiramo novu instancu repozitorija nad istom perzistentnom Room bazom
        val newRepositoryInstance = DemoTenderRepository(database, testScope)

        // 7. Verifikacija perzistentnosti (Verify saved tender & profile after restart)
        val profileAfterRestart = newRepositoryInstance.getCompanyProfile().first()
        assertEquals("Zenica Elektro Inženjering d.o.o.", profileAfterRestart.companyName)

        val savedIdsAfterRestart = newRepositoryInstance.getSavedTenderIds().first()
        assertTrue("Spremljeni tender mora ostati perzistiran u Room bazi nakon restarta",
            savedIdsAfterRestart.contains("TND-BA-2026-001"))

        val openedTenderAfterRestart = newRepositoryInstance.getTenderById("TND-BA-2026-001")
        assertNotNull(openedTenderAfterRestart)
        assertEquals("Nabavka i ugradnja pametne LED javne rasvjete na području Grada Zenica", openedTenderAfterRestart?.title)
    }
}
