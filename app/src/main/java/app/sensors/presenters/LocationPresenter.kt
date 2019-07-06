package app.sensors.presenters

import android.location.Location
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import app.sensors.R
import app.sensors.listeners.LocationListener
import app.sensors.modules.LocationModule

class LocationPresenter(var details_loc: TextView, var activity: AppCompatActivity, private var listeners: ArrayList<LocationListener>) : Presenter{
    override var started: Boolean = false
    var mHandler: Handler
    var locationModule: LocationModule
    lateinit var vdRunnable: Runnable

    init {
        locationModule = LocationModule(activity, listeners)
        mHandler = Handler()
    }

    /**
     * Start task running every second to update location
     */
    override fun start() {
        started = true
        vdRunnable = object : Runnable {
            override fun run() {
                if(locationModule.LOC_enu_GetCurrentLocation() != null) {
                    details_loc.setText(activity.getString(R.string.location_value,
                        locationModule.LOC_enu_GetCurrentLocation()?.latitude,
                        locationModule.LOC_enu_GetCurrentLocation()?.longitude))
                } else {
                    details_loc.setText(R.string.loading)
                }
                mHandler.postDelayed(this, 1000)
            }
        }

        locationModule.locationUpdateState = true
        locationModule.startLocationUpdates()
        mHandler.post(vdRunnable)
    }

    /**
     * Stop runnable task
     */
    override fun stop() {
        started = false
        mHandler.removeCallbacks(vdRunnable)
    }
}