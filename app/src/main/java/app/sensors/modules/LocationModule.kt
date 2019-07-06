package app.sensors.modules

import android.annotation.SuppressLint
import android.app.Activity
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import app.sensors.listeners.LocationListener
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*


class LocationModule(private var activity: Activity, private var listeners: ArrayList<LocationListener>) {
    private val TAG = "LocationModule"
    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
    private var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    var locationUpdateState = false
    val REQUEST_CHECK_SETTINGS = 2
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    lateinit var location: Location

    init {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                Log.d(TAG, "Location updated: " + p0.lastLocation.altitude.toString())
                location = p0.lastLocation
                for (listener in listeners) {
                    listener.onLocationChange(location)
                }
            }
        }
        createLocationRequest()
    }

    /**
     * Check if location permission is granted
     */
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

    /**
     * Start listening for location changes
     */
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


    /**
     * Returns Location if exists, null otherwise
     * @return location
     */
    fun LOC_enu_GetCurrentLocation(): Location? {
        if(::location.isInitialized)
            return location
        else
            return null
    }

}
