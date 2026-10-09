package com.example.domain.util

import com.example.data.model.Tender

enum class UrgencyLevel {
    CRITICAL, // 0..3 days
    WARNING,  // 4..7 days
    NORMAL,   // > 7 days
    EXPIRED,  // < 0 days or finished
    UNKNOWN   // missing date
}

data class DeadlineInfo(
    val statusText: String,
    val badgeLabel: String,
    val isExpired: Boolean,
    val isUrgent: Boolean,
    val daysRemaining: Int?,
    val urgencyLevel: UrgencyLevel
)

object DeadlineHelper {

    fun parseDeadline(tender: Tender): DeadlineInfo {
        if (tender.deadlineDate.isBlank()) {
            return DeadlineInfo(
                statusText = "Rok nije dostupan",
                badgeLabel = "N/A",
                isExpired = false,
                isUrgent = false,
                daysRemaining = null,
                urgencyLevel = UrgencyLevel.UNKNOWN
            )
        }

        if (tender.status.equals("Završen", ignoreCase = true) || tender.daysUntilDeadline < 0) {
            return DeadlineInfo(
                statusText = "ROK ISTEKAO",
                badgeLabel = "ISTEKAO",
                isExpired = true,
                isUrgent = false,
                daysRemaining = 0,
                urgencyLevel = UrgencyLevel.EXPIRED
            )
        }

        val days = tender.daysUntilDeadline

        return when {
            days == 0 -> DeadlineInfo(
                statusText = "ROK ISTIČE DANAS!",
                badgeLabel = "DANAS",
                isExpired = false,
                isUrgent = true,
                daysRemaining = 0,
                urgencyLevel = UrgencyLevel.CRITICAL
            )
            days == 1 -> DeadlineInfo(
                statusText = "ROK ISTIČE SUTRA!",
                badgeLabel = "SUTRA",
                isExpired = false,
                isUrgent = true,
                daysRemaining = 1,
                urgencyLevel = UrgencyLevel.CRITICAL
            )
            days in 2..3 -> DeadlineInfo(
                statusText = "HITNO: Još $days dana",
                badgeLabel = "JOŠ $days D.",
                isExpired = false,
                isUrgent = true,
                daysRemaining = days,
                urgencyLevel = UrgencyLevel.CRITICAL
            )
            days in 4..7 -> DeadlineInfo(
                statusText = "PAŽNJA: Još $days dana",
                badgeLabel = "JOŠ $days D.",
                isExpired = false,
                isUrgent = true,
                daysRemaining = days,
                urgencyLevel = UrgencyLevel.WARNING
            )
            days > 7 -> DeadlineInfo(
                statusText = "Preostalo još $days dana",
                badgeLabel = "JOŠ $days D.",
                isExpired = false,
                isUrgent = false,
                daysRemaining = days,
                urgencyLevel = UrgencyLevel.NORMAL
            )
            else -> DeadlineInfo(
                statusText = "ROK ISTEKAO",
                badgeLabel = "ISTEKAO",
                isExpired = true,
                isUrgent = false,
                daysRemaining = 0,
                urgencyLevel = UrgencyLevel.EXPIRED
            )
        }
    }

    fun isUrgent(tender: Tender): Boolean {
        val info = parseDeadline(tender)
        return info.isUrgent
    }

    fun formatEstimatedValue(valueKm: Double): String {
        return if (valueKm > 0) {
            java.lang.String.format(java.util.Locale.GERMAN, "%,.0f KM", valueKm)
        } else {
            "Podatak nije dostupan."
        }
    }
}
