package com.mshaw.yelptest.ui.details

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.google.android.material.chip.Chip
import com.mshaw.yelptest.R
import com.mshaw.yelptest.databinding.ActivityDetailsBinding
import com.mshaw.yelptest.models.Business

class DetailsActivity: AppCompatActivity() {
    companion object {
        const val BUSINESS = "business"
    }

    private val binding: ActivityDetailsBinding by lazy {
        ActivityDetailsBinding.inflate(layoutInflater)
    }

    private val viewModel: DetailsViewModel by viewModels()
    private var business: Business? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        business = intent.getParcelableExtra(BUSINESS)

        binding.business = business
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}