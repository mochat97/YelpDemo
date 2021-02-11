package com.mshaw.yelptest.util.extensions

import com.mshaw.yelptest.util.state.AwaitResult
import com.mshaw.yelptest.util.state.ErrorResponse
import com.squareup.moshi.Moshi
import retrofit2.HttpException
import retrofit2.Response

fun <T> Response<T>.awaitResult(): AwaitResult<T> {
    return try {
        if (isSuccessful) {
            val body = body()
            if (body == null) {
                AwaitResult.Error(NullPointerException("Response body is null"), raw(), errorBody()?.string())
            } else {
                AwaitResult.Ok(body, raw())
            }
        } else {
            AwaitResult.Error(HttpException(this), raw(), errorBody()?.string())
        }
    } catch (e: Exception) {
        val json = errorBody()?.string()
        val errorResponse = if (json != null) {
            Moshi.Builder().build().adapter(ErrorResponse::class.java).fromJson(json)
        } else null

        AwaitResult.Error(e, raw(), errorBody()?.string(), errorResponse)
    }
}

val <T> Response<T>.value: T
    get() = try {
        body() as T
    } catch (e: Exception) {
        throw HttpException(this)
    }