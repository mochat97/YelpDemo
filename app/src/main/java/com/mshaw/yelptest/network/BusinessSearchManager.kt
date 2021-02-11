package com.mshaw.yelptest.network

import com.mshaw.yelptest.models.BusinessSearchResponse
import com.mshaw.yelptest.util.extensions.awaitResult
import com.mshaw.yelptest.util.state.AwaitResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BusinessSearchManager @Inject constructor(private val service: BusinessSearchService) {
    suspend fun getBusinessesInArea(term: String, lat: Double, lng: Double): AwaitResult<BusinessSearchResponse> =
        withContext(Dispatchers.IO) { service.getBusinessesInArea(term, lat, lng).awaitResult() }
}