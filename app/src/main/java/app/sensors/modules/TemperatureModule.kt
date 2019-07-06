package app.sensors.modules

import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.SensorEventListener
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import app.sensors.R
import app.sensors.listeners.LocationListener
import app.sensors.models.Constants
import app.sensors.services.WeatherService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.util.*




class TemperatureModule: LocationListener {
    private val TAG = "TemperatureModule"
    var sensorManager: SensorManager
    var activity: SensorEventListener
    var sensorExists = false;
    var temperature: Float?

    val weatherServe by lazy {
        WeatherService.create()
    }
    var disposable: Disposable? = null

    constructor(sensorManager: SensorManager, activity: SensorEventListener) {
        this.sensorManager = sensorManager;
        this.activity = activity
        this.temperature = null

        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

        if (sensor != null) {
            sensorExists = true
            sensorManager.registerListener(activity, sensor, SensorManager.SENSOR_DELAY_FASTEST)
            Toast.makeText(activity as AppCompatActivity, R.string.temperature_sensor_found, LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity as AppCompatActivity, R.string.temperature_sensor_not_found, LENGTH_SHORT).show()
        }
    }

    /**
     * Listen to location changes if sensor does not exist.
     * Call OpenWeather API based on location
     */
    override fun onLocationChange(location: Location) {
        if(sensorExists.not()) {
            try {
                val gcd = Geocoder(activity as AppCompatActivity, Locale.getDefault())
                val addresses = gcd.getFromLocation(location.latitude, location.longitude, 1)
                if (addresses.size > 0) {
                    var currentCity = addresses[0].adminArea
                    if (currentCity.contains(' ')) {
                        currentCity = currentCity.split(' ')[0]
                    }
                    val currentCountryCode = addresses[0].countryCode
                    disposable = weatherServe.getWeatherByCityName(
                        currentCity
                                + "," + currentCountryCode, Constants.OPEN_WEATHER_API_KEY
                    )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            { result ->
                                temperature = convertKelvinToCelsius(result.main.temp)
                                Log.d(TAG, temperature.toString())
                            },
                            { error ->
                                Log.e(TAG, error.message)

                            })
                }
            } catch (ioException: IOException) {
                // Catch network or other I/O problems.
                Log.e(TAG, ioException.message)
            } catch (illegalArgumentException: IllegalArgumentException) {
                // Catch invalid latitude or longitude values.
                Log.e(TAG, illegalArgumentException.message)
            }
        } else {
            Toast.makeText(activity as AppCompatActivity, R.string.location_undefined, LENGTH_SHORT).show()
        }
    }

    /**
     * Stop listening to sensor changes
     */
    fun unregister() {
        sensorManager.unregisterListener(activity);
        disposable?.dispose()
    }


    /**
     * Returns temperature in Fahrenheit
     * @return temperature in Float
     */
    fun TMP_u32_GetCurrentTempInFahrenheit(): Float? {
        if(temperature != null)
            return convertCelsiusToFahrenheit(temperature)
        else
            return null
    }

    /**
     * Returns temperature in Celsius
     * @return temperature in Float
     */
    fun TMP_u32_GetCurrentTempInCelsius(): Float? {
        if(temperature != null)
            return temperature
        else
            return null
    }

    /**
     * Returns temperature in Kelvin
     * @return temperature in Float
     */
    fun TMP_u32_GetCurrentTempInKelvin(): Float? {
        if(temperature != null)
            return convertCelsiusToKelvin(temperature)
        else
            return null
    }

    /**
     * Converts temperature from Celsius to Fahrenheit
     * @param temperature in Float
     * @return temperature in Float
     */
    fun convertCelsiusToFahrenheit(temp: Float?) = (9/5f * temp!!) + 32

    /**
     * Converts temperature from Celsius to Kelvin
     * @param temperature in Float
     * @return temperature in Float
     */
    fun convertCelsiusToKelvin(temp: Float?) = temp!! + 273.15f

    /**
     * Converts temperature from Kelvin to Celsius
     * @param temperature in Float
     * @return temperature in Float
     */
    fun convertKelvinToCelsius(temp: Float?) = temp!! - 273.15f

}