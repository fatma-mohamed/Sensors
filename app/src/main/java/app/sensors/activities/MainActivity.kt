package app.sensors.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.sensors.R
import app.sensors.presenters.TimeAndDatePresenter
import app.sensors.presenters.VerticalDisplacementPresenter
import kotlinx.android.synthetic.main.activity_main.*




class MainActivity : AppCompatActivity() {
    lateinit var timeAndDatePresenter: TimeAndDatePresenter
    lateinit var verticalDisplacementPresenter: VerticalDisplacementPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        timeAndDatePresenter = TimeAndDatePresenter(tnd_current_time, tnd_current_date,
            tnd_current_time_and_date)

        verticalDisplacementPresenter = VerticalDisplacementPresenter(details_vd, this)
    }

    override fun onPause() {
        stop()
        super.onPause()
    }

    override fun onResume() {
        start()
        super.onResume()
    }

    fun start() {
        timeAndDatePresenter.start()
        verticalDisplacementPresenter.start()
    }

    fun stop() {
        timeAndDatePresenter.stop()
        verticalDisplacementPresenter.stop()
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

}
