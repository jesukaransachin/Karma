package com.ingrammicro.helpme.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.ingrammicro.helpme.BuildConfig
import com.ingrammicro.helpme.R
import com.ingrammicro.helpme.utils.SessionManager

private const val TAG = "BaseFragment"
private const val REQUEST_LOCATION_PERMISSIONS = 34

open class BaseFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    fun checkLocationPermissions() =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    fun startLocationPermissionRequest() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION_PERMISSIONS
        )
    }

    fun requestLocationPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            // Provide an additional rationale to the user. This would happen if the user denied the
            // request previously, but didn't check the "Don't ask again" checkbox.
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            startLocationPermissionRequest()
        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            Log.i(TAG, "Requesting permission")
            startLocationPermissionRequest()
        }
    }

    private fun checkLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_LOCATION_PERMISSIONS) {
            when {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                grantResults.isEmpty() -> {
                    Log.i(TAG, "User interaction was cancelled.")
                }

                // Permission granted.
                (grantResults[0] == PackageManager.PERMISSION_GRANTED) -> {
                    Log.d(TAG, "Permission was granted.")
                    getDeviceLocation()
                }

                // Permission denied.
                else -> {
                    Log.d(TAG, "Permission denied.")
//                    showAlertDialog()
                }
            }
        }
    }

    fun checkLocationSettings(): Boolean {

        if (!checkLocationPermissions()) {
            startLocationPermissionRequest()
            return false
        }

        if (!checkLocationEnabled()) {
            showLocationAlertDialog()
            return false
        }

        return true
    }

    @SuppressLint("MissingPermission")
    fun getDeviceLocation() {

        val sessionManager = SessionManager(requireContext())
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                // Got last known location. In some rare situations this can be null.
                location?.let {
                    Log.d(TAG, "Location: ${it.latitude}, ${it.longitude}")
                    sessionManager.saveUserLat(it.latitude.toString())
                    sessionManager.saveUserLng(it.longitude.toString())
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Location: ${exception}")
            }
    }

    private fun showAlertDialog() {

        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(R.string.permission_denied_explanation)

        builder.setPositiveButton(R.string.settings) { _, _ ->
            // Build intent that displays the App settings screen.
            val intent = Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }

        builder.setNegativeButton(R.string.cancel) { _, _ ->

        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }

    private fun showRationalAlertDialog() {

        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(R.string.permission_rationale)

        builder.setPositiveButton(R.string.ok) { _, _ ->
            // Request permission
            startLocationPermissionRequest()
        }

        builder.setNegativeButton(R.string.cancel) { _, _ ->

        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }

    private fun showLocationAlertDialog() {

        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(R.string.location_not_enabled)

        builder.setPositiveButton(R.string.settings) { _, _ ->
            // Build intent that displays the Location settings screen.
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        builder.setNegativeButton(R.string.cancel) { _, _ ->

        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }
}