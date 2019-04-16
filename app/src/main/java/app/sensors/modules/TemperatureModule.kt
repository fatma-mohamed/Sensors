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
import app.sensors.utils.NetworkUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*


class TemperatureModule: LocationListener {
    var sensorManager: SensorManager
    var activity: SensorEventListener
    var locationModule: LocationModule?
    var temperature: Float?
    var sensorExists = false;

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
            this.locationModule = null
        } else {
            Toast.makeText(activity as AppCompatActivity, R.string.temperature_sensor_not_found, LENGTH_SHORT).show()
            this.locationModule = LocationModule(activity as AppCompatActivity, this)
            this.locationModule?.locationUpdateState = true
            this.locationModule?.startLocationUpdates()
        }
    }

    override fun onLocationChange(location: Location) {
        val gcd = Geocoder(activity as AppCompatActivity, Locale.getDefault())
        val addresses = gcd.getFromLocation(location.latitude, location.longitude, 1)
        if (addresses.size > 0) {
            var currentCity = addresses[0].adminArea
            if(currentCity.contains(' ')) {
                currentCity = currentCity.split(' ')[0]
            }
            val currentCountryCode = addresses[0].countryCode
            disposable =  weatherServe.getWeatherByCityName(currentCity
                    +","+currentCountryCode, Constants.WEATHER_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        temperature = convertKelvinToCelsius(result.main.temp)
                        Log.d("TEMP", temperature.toString())
                    },
                    { error ->
                        Log.e("TemperatureModule", error.message)

                    })
        } else {
            Toast.makeText(activity as AppCompatActivity, R.string.location_undefined, LENGTH_SHORT).show()
        }
    }

    fun unregister() {
        sensorManager.unregisterListener(activity);
        disposable?.dispose()
    }


    fun TMP_u32_GetCurrentTempInFahrenheit(): Float? {
        if(temperature != null)
            return convertCelsiusToFahrenheit(temperature)
        else
            return null
    }

    fun TMP_u32_GetCurrentTempInCelsius(): Float? {
        if(temperature != null)
            return temperature
        else
            return null
    }

    fun TMP_u32_GetCurrentTempInKelvin(): Float? {
        if(temperature != null)
            return convertCelsiusToKelvin(temperature)
        else
            return null
    }


    fun convertCelsiusToFahrenheit(temp: Float?) = (9/5f * temp!!) + 32
    fun convertCelsiusToKelvin(temp: Float?) = temp!! + 273.15f
    fun convertKelvinToCelsius(temp: Float?) = temp!! - 273.15f

}