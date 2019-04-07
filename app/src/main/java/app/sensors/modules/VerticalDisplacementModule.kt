package app.sensors.modules

import android.annotation.SuppressLint
import android.app.Activity
import android.content.IntentSender
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*


class VerticalDisplacementModule{
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var activity: Activity

    private var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    var locationUpdateState = false
    var altitude = 0.0
    val REQUEST_CHECK_SETTINGS = 2
    val LOCATION_PERMISSION_REQUEST_CODE = 1

    constructor(activity: Activity)  {
        this.activity = activity
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                Log.d("VD", "Altitude updated: " + p0.lastLocation.altitude.toString())
                altitude = p0.lastLocation.altitude
            }
        }
        createLocationRequest()
    }

    private fun checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        checkLocationPermissions()
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)


        val client = LocationServices.getSettingsClient(activity)
        val task = client.checkLocationSettings(builder.build())


        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(activity,
                        REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }


    fun VP_s64_GetCurrentVerticalPosition(): Double {
        return altitude
    }

}
