package app.sensors.activities

import android.app.Activity
import android.content.Intent
import android.hardware.SensorEventListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.hardware.SensorManager
import android.content.Context
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.net.ConnectivityManager
import android.widget.Toast
import app.sensors.R
import app.sensors.listeners.LocationListener
import app.sensors.presenters.*
import app.sensors.utils.NetworkUtils


class MainActivity : AppCompatActivity(), SensorEventListener, NetworkUtils.ConnectivityReceiverListener {
    lateinit var timeAndDatePresenter: TimeAndDatePresenter
    lateinit var verticalDisplacementPresenter: VerticalDisplacementPresenter
    lateinit var temperaturePresenter: TemperaturePresenter
    lateinit var locationPresenter: LocationPresenter
    lateinit var speedPresenter: SpeedPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(app.sensors.R.layout.activity_main)

        registerReceiver(NetworkUtils(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )

        timeAndDatePresenter = TimeAndDatePresenter(tnd_current_time, tnd_current_date,
            tnd_current_time_and_date)
        verticalDisplacementPresenter = VerticalDisplacementPresenter(details_vd, this)
        temperaturePresenter = TemperaturePresenter(temp_celsius, temp_fahrenhit, temp_kelvin,
            getSystemService(Context.SENSOR_SERVICE) as SensorManager, this)
        speedPresenter = SpeedPresenter(speed_km_hr, speed_mile_hr,
            speed_meter_sec, speed_acceleration, getSystemService(Context.SENSOR_SERVICE) as SensorManager, this);


        var listeners = arrayListOf<LocationListener>();
        listeners.add(verticalDisplacementPresenter.verticalDisplacementModule)
        listeners.add(temperaturePresenter.temperatureModule)
        listeners.add(speedPresenter.speedModule)
        locationPresenter = LocationPresenter(details_loc, this,
            listeners)
    }

    override fun onPause() {
        stop()
        super.onPause()
    }

    override fun onResume() {
        start()
        NetworkUtils.connectivityReceiverListener = this
        super.onResume()
    }

    /**
     * Start modules' runnable tasks
     */
    fun start() {
        timeAndDatePresenter.start()
        verticalDisplacementPresenter.start()
        temperaturePresenter.start()
        locationPresenter.start()
        speedPresenter.start()
    }

    /**
     * Stop modules' runnable tasks
     */
    fun stop() {
        timeAndDatePresenter.stop()
        verticalDisplacementPresenter.stop()
        temperaturePresenter.stop()
        locationPresenter.stop()
        speedPresenter.stop()
    }

    /**
     * Listen to permission requests
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == locationPresenter.locationModule.REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationPresenter.locationModule.locationUpdateState = true
                locationPresenter.locationModule.startLocationUpdates()
            }
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    /**
     * Listen to sensor changes
     */
    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && event.values.isNotEmpty()) {
            if(event.sensor?.stringType!!.contains("linear_acceleration")) {
                val alpha = 0.8f;
                speedPresenter.speedModule.gravity = alpha * speedPresenter.speedModule.gravity + (1 - alpha) * event.values[0];

                var acceleration = event.values[0] - speedPresenter.speedModule.gravity;
                speedPresenter.speedModule.acceleration = acceleration
            } else {
                var temperature = event.values[0];
                temperaturePresenter.temperatureModule.temperature = temperature
            }
        }
    }

    /**
     * Listen to Network changes
     */
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if(!isConnected) {
            this.temperaturePresenter.stop()
            Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show()
        } else {
            if(!this.temperaturePresenter.started)
                this.temperaturePresenter.start()
        }
    }


}
