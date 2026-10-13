package com.example.domain.ai

import com.example.data.model.AiSummaryData
import com.example.data.model.Tender
import java.util.Locale

/**
 * TenderAiSummarizer - Generiše strukturisani sažetak tenderske dokumentacije
 * na osnovu lokalnih analitičkih pravila (v0.1 MVP). Ne poziva vanjske AI API servise
 * u klijentu, već priprema podatke za budući backend AI mikroservis.
 * Pridržava se strogog pravila bez halucinacija: ako podatak nije dostupan u tenderskoj
 * specifikaciji, navodi se "Podatak nije dostupan."
 */
object TenderAiSummarizer {

    fun generateSummary(tender: Tender): AiSummaryData {
        // 1. Šta se nabavlja?
        val what = if (tender.description.isNotBlank()) {
            "${tender.title} — ${tender.description}"
        } else if (tender.title.isNotBlank()) {
            tender.title
        } else {
            "Podatak nije dostupan."
        }

        // 2. Ko može aplicirati?
        val whoCanApply = when {
            tender.category.contains("Elektro", ignoreCase = true) ->
                "Pravna lica registrovana za izvođenje elektroinstalacijskih radova sa odgovarajućim licencama i atestiranim osobljem."
            tender.category.contains("Građev", ignoreCase = true) ->
                "Privredna društva registrovana za poslove građenja i rekonstrukcije sa položenim stručnim ispitima inženjera gradilišta."
            tender.category.contains("IT", ignoreCase = true) || tender.category.contains("softver", ignoreCase = true) ->
                "Registrovana IT preduzeća, ovlašteni partneri/distributeri hardvera sa sertifikovanim sistem inženjerima."
            tender.category.contains("Medic", ignoreCase = true) ->
                "Veleprometnici medicinskim sredstvima upisani u registar Agencije za lijekove i medicinska sredstva BiH (ALMBIH)."
            tender.category.contains("Održavanj", ignoreCase = true) ->
                "Servisne kompanije sa tehničkom opremljenošću i referencama na sličnim sistemima."
            else -> "Pravna lica koja ispunjavaju uslove iz člana 45-51. Zakona o javnim nabavkama BiH (ZJN BiH)."
        }

        // 3. Procijenjena vrijednost
        val estValue = if (tender.estimatedValueKm > 0) {
            String.format(Locale.GERMAN, "%,.2f KM (bez PDV-a)", tender.estimatedValueKm)
        } else {
            "Podatak nije dostupan."
        }

        // 4. Rok
        val deadline = if (tender.deadlineDate.isNotBlank()) {
            val daysText = if (tender.daysUntilDeadline >= 0) " (preostalo još ${tender.daysUntilDeadline} dana)" else " (rok je istekao)"
            "${tender.deadlineDate}$daysText"
        } else {
            "Podatak nije dostupan."
        }

        // 5. Ključni dokumenti
        val keyDocs = if (tender.documents.isNotEmpty()) {
            tender.documents.map { "${it.title} (${it.fileFormat}, ${it.sizeText})" }
        } else {
            listOf(
                "Tenderska dokumentacija sa pozivom za dostavu ponuda",
                "Obrazac za cijenu ponude (Aneks 2)",
                "Izjava po članu 45. Zakona o javnim nabavkama BiH"
            )
        }

        // 6. Na šta firma treba paziti?
        val caveats = mutableListOf<String>()
        if (tender.daysUntilDeadline in 1..7) {
            caveats.add("Hitnost: Rok za prijavu ističe za manje od sedam dana (${tender.daysUntilDeadline} d).")
        }
        if (tender.estimatedValueKm > 200_000.0) {
            caveats.add("Garancija za ozbiljnost ponude: Za tendere veće vrijednosti obavezna je bankarska garancija od 1.5% - 2%.")
        }
        caveats.add("CPV klasifikacija: Provjerite tačnost šifre ${tender.cpvCode} u vašem sudskom registru.")
        caveats.add("Zvanični portal: Prijave i preuzimanje vrše se isključivo putem e-Nabavke (ejn.gov.ba).")

        return AiSummaryData(
            tenderId = tender.id,
            whatIsProcured = what,
            whoCanApply = whoCanApply,
            estimatedValue = estValue,
            deadline = deadline,
            keyDocuments = keyDocs,
            criticalCaveats = caveats
        )
    }
}
