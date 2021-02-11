package com.mshaw.yelptest.binding

import android.view.ContextThemeWrapper
import android.widget.ImageView
import android.widget.RatingBar
import androidx.databinding.BindingAdapter
import coil.load
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.mshaw.yelptest.R

@BindingAdapter("imageUrl")
fun setImageUrl(view: ImageView, url: String?) {
    if (url == null || url.isEmpty()) return
    view.load(url)
}

@BindingAdapter("rating")
fun setRating(view: RatingBar, rating: Double) {
    view.rating = rating.toFloat()
}

@BindingAdapter("chips")
fun chips(chipView: ChipGroup, titles: List<String>) {
    val contextThemeWrapper = ContextThemeWrapper(chipView.context, R.style.TransparentChip)
    titles.forEach {
        val chip = Chip(contextThemeWrapper, null, 0).apply {
            text = it
        }

        chipView.addView(chip)
    }
}