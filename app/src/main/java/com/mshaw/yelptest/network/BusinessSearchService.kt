package com.mshaw.yelptest.network

import com.mshaw.yelptest.models.BusinessSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BusinessSearchService {
    @GET("/v3/businesses/search")
    suspend fun getBusinessesInArea(@Query("term") term: String,
                                    @Query("latitude") lat: Double,
                                    @Query("longitude") lng: Double): Response<BusinessSearchResponse>
}