package com.mshaw.yelptest.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mshaw.yelptest.models.Business
import com.mshaw.yelptest.ui.details.DetailsActivity.Companion.BUSINESS
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle): ViewModel() {
    val business: Business?
        get() = savedStateHandle.get(BUSINESS)


}