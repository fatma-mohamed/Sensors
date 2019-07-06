package app.sensors.modules

import android.app.Activity
import android.location.Location
import android.util.Log
import app.sensors.listeners.LocationListener


class VerticalDisplacementModule: LocationListener{
    private val TAG = "VDModule"
    private var activity: Activity
    var altitude = 0.0

    constructor(activity: Activity)  {
        this.activity = activity
    }


    /**
     * Listen to location changes if sensor does not exist.
     * Call OpenWeather API based on location
     */
    override fun onLocationChange(location: Location) {
        Log.d(TAG, "Altitude updated: " + location.altitude.toString())
        altitude = location.altitude
    }

    /**
     * Returns altitude
     * @return altitude in Double
     */
    fun VP_s64_GetCurrentVerticalPosition(): Double {
        return altitude
    }

}
