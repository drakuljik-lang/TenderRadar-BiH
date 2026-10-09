package com.example.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TenderApi {

    @GET("ProcurementNotices")
    suspend fun getProcurementNotices(
        @Query("\$top") top: Int = 50,
        @Query("\$skip") skip: Int = 0,
        @Query("\$filter") filter: String? = null,
        @Query("\$orderby") orderBy: String? = null,
        @Query("ietfTag") ietfTag: String = "bs-latn-ba"
    ): Response<ODataResponse<ProcurementNoticeDto>>
}
