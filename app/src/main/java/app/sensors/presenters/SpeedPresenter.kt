package app.sensors.presenters

import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import app.sensors.R
import app.sensors.modules.SpeedModule

class SpeedPresenter : Presenter {
    override var started: Boolean = false
    var speed_km_hr: TextView;
    var speed_mile_hr: TextView;
    var speed_meter_sec: TextView;
    var speed_acceleration: TextView;
    var activity: AppCompatActivity
    var mHandler: Handler
    var speedModule: SpeedModule
    lateinit var speedRunnable: Runnable

    constructor(
        speed_km_hr: TextView,
        speed_mile_hr: TextView,
        speed_meter_sec: TextView,
        speed_acceleration: TextView,
        sensorManager: SensorManager,
        activity: AppCompatActivity
    ) {
        this.speed_km_hr = speed_km_hr
        this.speed_mile_hr = speed_mile_hr
        this.speed_meter_sec = speed_meter_sec
        this.speed_acceleration = speed_acceleration
        this.activity = activity
        speedModule = SpeedModule(sensorManager, activity as SensorEventListener)
        mHandler = Handler()
    }

    /**
     * Start task running every second to update location
     */
    override fun start() {
        started = true
        speedRunnable = object : Runnable {
            override fun run() {
                if(speedModule.ACC_u32_GetCurrentSpeedInKmPerHour() != null) {
                    speed_km_hr.setText(activity.getString(R.string.speed_km_hr,
                        speedModule.ACC_u32_GetCurrentSpeedInKmPerHour()))
                } else {
                    speed_km_hr.setText(R.string.loading)
                }

                if(speedModule.ACC_u32_GetCurrentSpeedInMilePerHour() != null) {
                    speed_mile_hr.setText(activity.getString(R.string.speed_mile_hr,
                        speedModule.ACC_u32_GetCurrentSpeedInMilePerHour()))
                } else {
                    speed_mile_hr.setText(R.string.loading)
                }

                if(speedModule.ACC_u32_GetCurrentSpeedInMeterPerSec() != null) {
                    speed_meter_sec.setText(activity.getString(R.string.speed_meter_sec,
                        speedModule.ACC_u32_GetCurrentSpeedInMeterPerSec()))
                } else {
                    speed_meter_sec.setText(R.string.loading)
                }

                if(speedModule.ACC_u32_GetCurrentAcceleration() != null && speedModule.ACC_u32_GetCurrentAcceleration() != Float.NaN) {
                    speed_acceleration.setText(activity.getString(R.string.speed_acceleration,
                        speedModule.ACC_u32_GetCurrentAcceleration()))
                } else {
                    speed_acceleration.setText(R.string.loading)
                }

                mHandler.postDelayed(this, 1000)
            }
        }

        mHandler.post(speedRunnable)
    }

    /**
     * Stop runnable task
     */
    override fun stop() {
        started = false
        mHandler.removeCallbacks(speedRunnable)
    }
}