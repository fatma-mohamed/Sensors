package app.sensors.presenters

import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import app.sensors.R
import app.sensors.modules.LocationModule
import app.sensors.modules.TemperatureModule

class TemperaturePresenter : Presenter {
    override var started: Boolean
    var temp_celsius: TextView;
    var temp_fahrenhit: TextView;
    var temp_kelvin: TextView;
    var activity: AppCompatActivity
    var mHandler: Handler
    var temperatureModule: TemperatureModule
    lateinit var temperatureRunnable: Runnable

    constructor(
        temp_celsius: TextView,
        temp_fahrenheit: TextView,
        temp_kelvin: TextView,
        sensorManager: SensorManager,
        activity: AppCompatActivity
    ) {
        started = false
        this.temp_celsius = temp_celsius
        this.temp_fahrenhit = temp_fahrenheit
        this.temp_kelvin = temp_kelvin
        this.temperatureModule = TemperatureModule(sensorManager, activity as SensorEventListener)
        this.mHandler = Handler()
        this.activity = activity
    }

    /**
     * Start task running every second to update location
     */
    override fun start() {
        started = true
        temperatureRunnable = object : Runnable {
            override fun run() {
                if(temperatureModule.TMP_u32_GetCurrentTempInCelsius() != null) {
                    temp_celsius.setText(activity.getString(R.string.temperature_value,
                        temperatureModule.TMP_u32_GetCurrentTempInCelsius(), "C"))
                } else {
                    temp_celsius.setText(R.string.loading)
                }

                if(temperatureModule.TMP_u32_GetCurrentTempInKelvin() != null) {
                    temp_kelvin.setText(activity.getString(R.string.temperature_value,
                        temperatureModule.TMP_u32_GetCurrentTempInKelvin(), "K"))
                } else {
                    temp_kelvin.setText(R.string.loading)
                }

                if(temperatureModule.TMP_u32_GetCurrentTempInFahrenheit() != null) {
                    temp_fahrenhit.setText(activity.getString(R.string.temperature_value,
                        temperatureModule.TMP_u32_GetCurrentTempInFahrenheit(), "F"))
                } else {
                    temp_fahrenhit.setText(R.string.loading)
                }

                mHandler.postDelayed(this, 1000)
            }
        }

        mHandler.post(temperatureRunnable)
    }

    /**
     * Stop runnable task
     */
    override fun stop() {
        started = false
        mHandler.removeCallbacks(temperatureRunnable)
        temperatureModule.unregister()
    }
}