package com.mshaw.yelptest.ui.list

import android.content.Context
import androidx.lifecycle.*
import com.mshaw.yelptest.models.BusinessSearchResponse
import com.mshaw.yelptest.network.BusinessSearchManager
import com.mshaw.yelptest.util.state.AwaitResult
import com.mshaw.yelptest.util.state.State
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val manager: BusinessSearchManager): ViewModel() {
    private val _businessSearchLiveData: MutableLiveData<State<BusinessSearchResponse>> = MutableLiveData()
    val businessSearchLiveData: LiveData<State<BusinessSearchResponse>> get() = _businessSearchLiveData

    fun getBusinessesInArea(term: String, lat: Double, lng: Double) {
        _businessSearchLiveData.value = State.Loading

        viewModelScope.launch {
            when (val result = manager.getBusinessesInArea(term, lat, lng)) {
                is AwaitResult.Ok -> {
                    _businessSearchLiveData.value = State.Success(result.value)
                }
                is AwaitResult.Error -> {
                    _businessSearchLiveData.value = State.Error(result.exception)
                }
            }
        }
    }
}