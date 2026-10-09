package com.example.data.remote

import com.example.data.model.CompanyProfile
import com.example.data.model.Tender
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * TenderApi - Pripremljen Retrofit interfejs za povezivanje sa zvaničnim
 * TenderRadar BiH backend serverom.
 * U trenutnoj MVP verziji koristi se lokalni Demo Repository sa Room bazom podataka.
 */
interface TenderApi {

    @GET("api/v1/tenders")
    suspend fun getTenders(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50,
        @Query("category") category: String? = null
    ): Response<List<Tender>>

    @GET("api/v1/tenders/{id}")
    suspend fun getTenderById(
        @Path("id") id: String
    ): Response<Tender>

    @GET("api/v1/tenders/search")
    suspend fun searchTenders(
        @Query("q") query: String,
        @Query("city") city: String? = null,
        @Query("cpv") cpv: String? = null
    ): Response<List<Tender>>

    @GET("api/v1/tenders/categories")
    suspend fun getCategories(): Response<List<String>>

    @POST("api/v1/company/profile")
    suspend fun syncCompanyProfile(
        @Body profile: CompanyProfile
    ): Response<CompanyProfile>

    @GET("api/v1/matches")
    suspend fun getMatches(
        @Query("companyId") companyId: String
    ): Response<List<Tender>>
}
