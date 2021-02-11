package com.mshaw.yelptest.models

import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import junit.framework.TestCase
import okio.Buffer
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class BusinessTest : TestCase() {
    private var business: Business? = null
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .add(Date::class.java, Rfc3339DateJsonAdapter())
        .build()

    @Before
    public override fun setUp() {
        super.setUp()
        business = try {
            val stream = javaClass.getResourceAsStream("/business.json") ?: return
            val reader = JsonReader.of(Buffer().readFrom(stream))
            moshi.adapter(Business::class.java).fromJson(reader)
        } catch (e: Exception) {
            null
        }
    }

    @Test
    fun shouldFormatPrice() {
        assert(business?.priceFormatted == "Price: $")
    }

    @Test
    fun shouldMapCategoryTitles() {
        assert(business?.categoryTitles?.isNotEmpty() == true)
        assert(business?.categoryTitles == listOf("Coffee & Tea"))
    }
}