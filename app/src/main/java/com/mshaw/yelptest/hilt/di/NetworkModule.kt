package com.mshaw.yelptest.hilt.di

import com.mshaw.yelptest.network.BusinessSearchManager
import com.mshaw.yelptest.network.BusinessSearchService
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {
    @Provides
    @Singleton
    fun providesMoshi(): Moshi = Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun providesRetrofit(moshi: Moshi): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                var request: Request = chain.request()
                val headers: Headers =
                    request.headers().newBuilder().add(
                        "Authorization",
                        "Bearer uQv-uMA1Fhk8p55vtrCsghJLmZeb-vhhWaoHTiaceN38WZp5_nILKhk7j6Iu04VvLvDpeEI64GFl_kMqLvDJ4dQCfdU1mE5AGLwEIEzJkHWTDpb5vTNMkW7N5_0hYHYx"
                    ).build()
                request = request.newBuilder().headers(headers).build()
                chain.proceed(request)
            }.build()

        return Retrofit.Builder()
            .baseUrl("https://api.yelp.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun providesBusinessSearchService(retrofit: Retrofit): BusinessSearchService = retrofit.create(
        BusinessSearchService::class.java
    )

    @Provides
    @Singleton
    fun providesBusinessSearchManager(service: BusinessSearchService) = BusinessSearchManager(
        service
    )
}