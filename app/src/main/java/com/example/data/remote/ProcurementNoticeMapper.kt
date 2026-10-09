package com.example.data.remote

import com.example.data.local.TenderEntity
import com.example.data.model.Tender
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private val BIH_ZONE = ZoneId.of("Europe/Sarajevo")
private val DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy.")

fun ProcurementNoticeDto.toEntity(): TenderEntity {
    val deadlineLocal = parseIsoDate(deadline)
    val publishedLocal = parseIsoDate(announced)
    val today = LocalDate.now(BIH_ZONE)

    val daysLeft: Int = deadlineLocal?.let {
        ChronoUnit.DAYS.between(today, it).toInt()
    } ?: 9999

    val expired = daysLeft < 0

    return TenderEntity(
        id = "ejn_$id",
        title = procedureName?.takeIf { it.isNotBlank() } ?: "(bez naziva)",
        institution = authorityName?.takeIf { it.isNotBlank() } ?: "Nepoznat narucilac",
        city = cityName?.takeIf { it.isNotBlank() } ?: "",
        canton = "",
        entity = "Federacija BiH",
        estimatedValueKm = 0.0,
        publishedDate = publishedLocal?.format(DATE_FORMAT) ?: "",
        deadlineDate = deadlineLocal?.format(DATE_FORMAT) ?: "",
        daysUntilDeadline = daysLeft,
        category = categoryName?.takeIf { it.isNotBlank() } ?: "Ostalo",
        cpvCode = "",
        cpvDescription = "",
        description = additionalInfo?.takeIf { it.isNotBlank() } ?: (procedureName ?: ""),
        documentsJson = "[]",
        sourceUrl = "https://ejn.gov.ba/Home/Detalji?Id=$id",
        isDemo = false,
        status = if (expired) "Zavrsen" else "Aktivan"
    )
}

fun TenderEntity.toTender(): Tender = Tender(
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
    documents = emptyList(),
    sourceUrl = sourceUrl,
    isDemo = isDemo,
    status = status
)

private fun parseIsoDate(iso: String?): LocalDate? {
    if (iso.isNullOrBlank()) return null
    return try {
        val datePart = iso.substringBefore("T").substringBefore(" ")
        LocalDate.parse(datePart)
    } catch (e: Exception) {
        null
    }
}
