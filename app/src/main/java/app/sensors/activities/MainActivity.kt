package app.sensors.activities

import android.app.Activity
import android.content.Intent
import android.hardware.SensorEventListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.sensors.presenters.TimeAndDatePresenter
import app.sensors.presenters.VerticalDisplacementPresenter
import kotlinx.android.synthetic.main.activity_main.*
import android.hardware.SensorManager
import android.content.Context
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.net.ConnectivityManager
import android.widget.Toast
import app.sensors.R
import app.sensors.presenters.LocationPresenter
import app.sensors.presenters.TemperaturePresenter
import app.sensors.utils.NetworkUtils


class MainActivity : AppCompatActivity(), SensorEventListener, NetworkUtils.ConnectivityReceiverListener {
    lateinit var timeAndDatePresenter: TimeAndDatePresenter
    lateinit var verticalDisplacementPresenter: VerticalDisplacementPresenter
    lateinit var temperaturePresenter: TemperaturePresenter
    lateinit var locationPresenter: LocationPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(app.sensors.R.layout.activity_main)

        registerReceiver(NetworkUtils(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )

        timeAndDatePresenter = TimeAndDatePresenter(tnd_current_time, tnd_current_date,
            tnd_current_time_and_date)
        verticalDisplacementPresenter = VerticalDisplacementPresenter(details_vd, this)
        locationPresenter = LocationPresenter(details_loc, this)
        temperaturePresenter = TemperaturePresenter(temp_celsius, temp_fahrenhit, temp_kelvin,
            getSystemService(Context.SENSOR_SERVICE) as SensorManager, this)

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

    fun start() {
        timeAndDatePresenter.start()
        verticalDisplacementPresenter.start()
        temperaturePresenter.start()
        locationPresenter.start()
    }

    fun stop() {
        timeAndDatePresenter.stop()
        verticalDisplacementPresenter.stop()
        temperaturePresenter.stop()
        locationPresenter.stop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == verticalDisplacementPresenter.verticalDisplacementModule.REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                verticalDisplacementPresenter.verticalDisplacementModule.locationUpdateState = true
                verticalDisplacementPresenter.verticalDisplacementModule.startLocationUpdates()
            }
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && event.values.isNotEmpty()) {
            var temperature = event.values[0];
            temperaturePresenter.temperatureModule.temperature = temperature
        }
    }


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
