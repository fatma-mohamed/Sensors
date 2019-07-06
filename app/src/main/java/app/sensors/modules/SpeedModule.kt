package app.sensors.modules

import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.SensorEventListener
import android.location.Location
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import app.sensors.listeners.LocationListener


class SpeedModule: LocationListener {
    private val TAG = "SpeedModule"
    var sensorManager: SensorManager
    var activity: SensorEventListener
    var sensorExists = false;
    var acceleration: Float?
    var lastSpeed: Float?
    var lastTime: Long?
    var gravity: Float = 0f
    var speed: Float?

    constructor(sensorManager: SensorManager, activity: SensorEventListener) {
        this.activity = activity
        this.sensorManager = sensorManager
        this.acceleration = null
        this.speed = null
        this.lastSpeed = null
        this.lastTime = null

        // Check if acceleration sensor exists
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        if (sensor != null) {
            sensorExists = true;
            sensorManager.registerListener(activity, sensor, SensorManager.SENSOR_DELAY_FASTEST)
            Toast.makeText(activity as AppCompatActivity, app.sensors.R.string.accelerator_sensor_found, LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity as AppCompatActivity, app.sensors.R.string.accelerator_sensor_not_found, LENGTH_SHORT).show()
        }
    }

    /**
     * If acceleration sensor exits, acceleration is calculated from MainActivity, else
     * it's calculated from speed over time
     * @param Location
     */
    override fun onLocationChange(location: Location) {
        if(sensorExists) {
            speed = location.speed
        } else {
            if(lastSpeed != null && lastTime != null) {
                val timeElapsed = (System.currentTimeMillis() - lastTime!!) / 1000
                speed = location.speed
                val speedDelta = speed!! - lastSpeed!!
                acceleration = speedDelta/timeElapsed
            }
            lastSpeed = location.speed
            lastTime = System.currentTimeMillis()
        }
    }


    /**
     * Returns speed in kilometers per hour
     * @return speed in Float
     */
    fun ACC_u32_GetCurrentSpeedInKmPerHour(): Float? {
        if(speed != null)
            return convertToKmPerHour(speed)
        else
            return null
    }


    /**
     * Returns speed in miles per hour
     * @return speed in Float
     */
    fun ACC_u32_GetCurrentSpeedInMilePerHour(): Float? {
        if(speed != null)
            return convertToMilePerHour(speed)
        else
            return null
    }


    /**
     * Returns speed in meters per hour
     * @return speed in Float
     */
    fun ACC_u32_GetCurrentSpeedInMeterPerSec(): Float? {
        if(speed != null)
            return speed
        else
            return null
    }


    /**
     * Returns acceleration in m/s2
     * @return acceleration in Float
     */
    fun ACC_u32_GetCurrentAcceleration(): Float? {
        if(acceleration != null)
            return acceleration
        else
            return null
    }


    /**
     * Converts speed from m/hr to km/hr
     * @param speed in Float
     * @return speed in Float
     */
    fun convertToKmPerHour(speed: Float?) = speed?.times(3.6f)

    /**
     * Converts speed from m/hr to mile/hr
     * @param speed in Float
     * @return speed in Float
     */
    fun convertToMilePerHour(speed: Float?) = speed?.times(2.237f)


}