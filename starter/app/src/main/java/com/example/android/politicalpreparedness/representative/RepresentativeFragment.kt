package com.example.android.politicalpreparedness.representative

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import java.util.*

class RepresentativeFragment : Fragment() {

    companion object {
        //DONE: Add Constant for Location request
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }


    //DONE: Declare ViewModel
    private val viewModel: RepresentativeViewModel by lazy {
        val application = requireNotNull(this.activity).application
        val viewModelFactory =  RepresentativeViewModel.Factory(application)
        ViewModelProvider(this,viewModelFactory)
            .get(RepresentativeViewModel::class.java)
    }

    //DONE: Establish bindings
    private lateinit var binding: FragmentRepresentativeBinding
    private lateinit var representativeAdapter: RepresentativeListAdapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_representative,
            container,
            false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.address = Address("", "", "", "", "")


        val states = resources.getStringArray(R.array.states)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, states)
        binding.state.adapter = adapter

        binding.buttonLocation.setOnClickListener {
            checkLocationPermissions(it)
        }

        //DONE: Establish button listeners for field and location search
        binding.buttonSearch.setOnClickListener {
            hideKeyboard()
            viewModel.getRepresentatives()
        }

        //DONE: Define and assign Representative adapter
        representativeAdapter = RepresentativeListAdapter()
        binding.representativesRecyclerView.adapter = representativeAdapter

        return binding.root

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //DONE: Populate Representative adapter
        viewModel.representatives.observe(viewLifecycleOwner, Observer { representatives ->
            representativeAdapter.submitList(representatives)

        })
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //DONE: Handle location permission result to get location on permission granted
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    getLocation()
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    var text = "To use this feature, the app need permissions from the user, " +
                            "which was not granted yet. If you change your mind, please change " +
                            "the permissions for this app."
                    val duration = Toast.LENGTH_LONG
                    val toast = Toast.makeText(requireContext(), text, duration)
                    toast.show()
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun checkLocationPermissions(view: View): Boolean {
        return if (isPermissionGranted()) {
            getLocation()
            true
        } else {
            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                getLocation()
            } else {
                requestPermissions(arrayOf(ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
            }
            false
        }


    }

    private fun isPermissionGranted(): Boolean {
        //DONE: Check if permission is already granted and return (true = granted, false = denied/other)
        return (ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        //DONE: Get location from LocationServices
        val locationClient: FusedLocationProviderClient = getFusedLocationProviderClient(requireContext())
        locationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val address = geoCodeLocation(location)
                    viewModel.address.value = address

                    val states = resources.getStringArray(R.array.states)
                    val selectedStateIndex = states.indexOf(address.state)
                    binding.state.setSelection(selectedStateIndex)

                    viewModel.getRepresentatives()
                }
            }
            .addOnFailureListener { e -> e.printStackTrace() }

    }

    private fun geoCodeLocation(location: Location): Address {
        val geocode = Geocoder(context, Locale.getDefault())
        return geocode.getFromLocation(location.latitude, location.longitude, 1)
            .map { address ->
                if (address.countryCode != "US") {
                    var text = "Unfortunately this API is for US only!"
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(requireContext(), text, duration)
                    toast.show()
                }
                Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
            }
            .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }
}