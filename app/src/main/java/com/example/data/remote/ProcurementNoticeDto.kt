package com.example.data.remote

import com.squareup.moshi.Json

data class ODataResponse<T>(
    @Json(name = "value") val value: List<T> = emptyList()
)

data class ProcurementNoticeDto(
    @Json(name = "Id") val id: Int = 0,
    @Json(name = "Number") val number: String? = null,
    @Json(name = "ContractingAuthorityName") val authorityName: String? = null,
    @Json(name = "ContractingAuthorityCityName") val cityName: String? = null,
    @Json(name = "ProcedureName") val procedureName: String? = null,
    @Json(name = "ProcedureNumber") val procedureNumber: String? = null,
    @Json(name = "ProcedureType") val procedureType: String? = null,
    @Json(name = "ContractType") val contractType: String? = null,
    @Json(name = "ContractCategoryName") val categoryName: String? = null,
    @Json(name = "AwardCriterion") val awardCriterion: String? = null,
    @Json(name = "ApplicationDeadlineDateTime") val deadline: String? = null,
    @Json(name = "DocumentationTakeOverDeadlineDate") val docDeadline: String? = null,
    @Json(name = "BidOpeningDateTime") val bidOpening: String? = null,
    @Json(name = "AdditionalInformation") val additionalInfo: String? = null,
    @Json(name = "Announced") val announced: String? = null,
    @Json(name = "LastUpdated") val lastUpdated: String? = null
)
