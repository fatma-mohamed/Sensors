package app.sensors.listeners

import android.location.Location

interface LocationListener{
    fun onLocationChange(location: Location)
}