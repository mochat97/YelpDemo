package com.mshaw.yelptest.ui.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mshaw.yelptest.models.BusinessSearchResponse
import com.mshaw.yelptest.network.BusinessSearchManager
import com.mshaw.yelptest.util.state.AwaitResult
import com.mshaw.yelptest.util.state.State
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.Response
import okio.Buffer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.lang.IllegalStateException
import java.util.*

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class MainViewModelTest : TestCase() {
    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var observer: Observer<in State<BusinessSearchResponse>>

    @Mock
    private lateinit var response: Response

    @get:Rule
    val testInstantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var businessSearchManager: BusinessSearchManager

    @Mock
    private lateinit var viewModel: MainViewModel

    private var successfulResponse: BusinessSearchResponse? = null
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .add(Date::class.java, Rfc3339DateJsonAdapter())
        .build()

    private val term = "starbucks"
    private val lat = 37.422740
    private val lng = -122.139956

    @Before
    public override fun setUp() {
        super.setUp()
        Dispatchers.setMain(testDispatcher)
        viewModel = MainViewModel(businessSearchManager)
        viewModel.businessSearchLiveData.observeForever(observer)

        successfulResponse = try {
            val stream = javaClass.getResourceAsStream("/business_search_response.json") ?: return
            val reader = JsonReader.of(Buffer().readFrom(stream))
            moshi.adapter(BusinessSearchResponse::class.java).fromJson(reader)
        } catch (e: Exception) {
            null
        }
    }

    @Test
    fun shouldParseJsonAsResponse() {
        val successfulResponse = successfulResponse
        if (successfulResponse == null) {
            fail("successResponse is null")
            return
        }

        assert(successfulResponse.businesses.isNotEmpty())
        assert(successfulResponse.businesses.size == 20)
        assert(successfulResponse.total == 442)
    }

    @Test
    fun shouldEmitSuccessState() = runBlocking {
        val successfulResponse = successfulResponse
        if (successfulResponse == null) {
            fail("successResponse is null")
            return@runBlocking
        }

        Mockito.`when`(businessSearchManager.getBusinessesInArea(term, lat, lng))
            .thenReturn(AwaitResult.Ok(successfulResponse, response))

        viewModel.getBusinessesInArea(term, lat, lng)
        Mockito.verify(observer).onChanged(State.Success(successfulResponse))
        viewModel.businessSearchLiveData.removeObserver(observer)
    }

    @Test
    fun shouldEmitErrorState() = runBlocking {
        val exception = Exception()
        Mockito.`when`(businessSearchManager.getBusinessesInArea(term, lat, lng)).thenReturn(AwaitResult.Error(exception))

        viewModel.getBusinessesInArea(term, lat, lng)
        Mockito.verify(observer).onChanged(State.Error(exception))
        viewModel.businessSearchLiveData.removeObserver(observer)
    }

    @After
    public override fun tearDown() {
        super.tearDown()
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}