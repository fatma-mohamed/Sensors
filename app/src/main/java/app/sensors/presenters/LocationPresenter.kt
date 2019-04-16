package app.sensors.presenters

import android.location.Location
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import app.sensors.R
import app.sensors.listeners.LocationListener
import app.sensors.modules.LocationModule

class LocationPresenter(var details_loc: TextView, var activity: AppCompatActivity) : Presenter, LocationListener{
    override var started: Boolean = false
    var mHandler: Handler
    var locationModule: LocationModule
    var location: Location?
    lateinit var vdRunnable: Runnable

    init {
        this.location = null
        locationModule = LocationModule(activity, this)
        mHandler = Handler()
    }

    override fun start() {
        started = true
        vdRunnable = object : Runnable {
            override fun run() {
                if(location != null) {
                    details_loc.setText(activity.getString(R.string.location_value,
                        location?.latitude,
                        location?.longitude))
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

    override fun onLocationChange(location: Location) {
        this.location = location
    }

    override fun stop() {
        started = false
        mHandler.removeCallbacks(vdRunnable)
    }
}