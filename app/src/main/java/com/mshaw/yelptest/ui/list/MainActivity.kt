package com.mshaw.yelptest.ui.list

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mshaw.yelptest.R
import com.mshaw.yelptest.adapters.BusinessSearchAdapter
import com.mshaw.yelptest.databinding.ActivityMainBinding
import com.mshaw.yelptest.models.Business
import com.mshaw.yelptest.util.state.State
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.app.ActivityCompat

import android.content.pm.PackageManager
import android.location.Location
import android.util.TypedValue
import android.view.inputmethod.EditorInfo
import android.widget.Toast

import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.mshaw.yelptest.ui.details.DetailsActivity
import com.mshaw.yelptest.util.EqualSpacingItemDecorator
import com.mshaw.yelptest.util.extensions.asString
import com.mshaw.yelptest.util.extensions.hideKeyboard
import com.mshaw.yelptest.util.extensions.withDismissButton

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), Listener {
    companion object {
        private const val locationRequestCode = 1000
    }

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewModel: MainViewModel by viewModels()

    private val adapter: BusinessSearchAdapter by lazy {
        BusinessSearchAdapter(this)
    }

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.businessSearchLiveData.observe(this) {
            when (it) {
                State.Loading -> {
                    binding.progressBar.show()
                }
                is State.Success -> {
                    binding.progressBar.hide()
                    updateRecyclerView(it.value.businesses)
                }
                is State.Error -> {
                    binding.progressBar.hide()
                }
            }
        }

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        setupBinding()
        fetchLocation()

        binding.searchButton.setOnClickListener {
            search()
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search()
            }
            true
        }
    }

    private fun setupBinding() {
        binding.progressBar.hide()
        val dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,16f, resources.displayMetrics).toInt()
        val recyclerView = binding.businessSearchRecyclerView

        recyclerView.addItemDecoration(EqualSpacingItemDecorator(dp))
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun search() {
        hideKeyboard(binding.searchEditText)
        val location = location
        if (location == null) {
            AlertDialog.Builder(this).withDismissButton()
                .setMessage("No location retrieved. Would you like to try again?")
                .setPositiveButton("Try again") { _, _ ->
                    fetchLocation()
                }
                .show()
            return
        }

        viewModel.getBusinessesInArea(binding.searchEditText.asString(), location.latitude, location.longitude)
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissionsRequested = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            ActivityCompat.requestPermissions(this, permissionsRequested, locationRequestCode)
        } else {
            Snackbar.make(binding.root, "Fetching location...", Snackbar.LENGTH_SHORT).show()
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                Snackbar.make(binding.root, "Location fetched", Snackbar.LENGTH_SHORT).show()
                this.location = location
            }.addOnFailureListener {
                Snackbar.make(binding.root, "Could not retrieve location.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateRecyclerView(businesses: List<Business>) {
        binding.businessSearchRecyclerView.isVisible = businesses.isNotEmpty()
        adapter.businesses = businesses
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            locationRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showPermissionEnabledState()
                    fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                        this.location = location
                    }
                } else {
                    showPermissionDeniedState()
                }
            }
        }
    }

    private fun showPermissionEnabledState() {
        binding.businessSearchRecyclerView.isVisible = true
        binding.enablePermissions.isVisible = false
        binding.searchButton.isEnabled = true
        binding.noResultsTextView.text = getString(R.string.no_results)
    }

    private fun showPermissionDeniedState() {
        binding.businessSearchRecyclerView.isVisible = false
        binding.enablePermissions.isVisible = true
        binding.searchButton.isEnabled = false
        binding.noResultsTextView.text = getString(R.string.permission_denied)
    }

    override fun onBusinessSelected(business: Business) {
        startActivity(Intent(this, DetailsActivity::class.java).apply {
            putExtra(DetailsActivity.BUSINESS, business)
        })
    }
}

interface Listener {
    fun onBusinessSelected(business: Business)
}