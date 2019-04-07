package app.sensors.presenters

import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import app.sensors.modules.VerticalDisplacementModule

class VerticalDisplacementPresenter : ModulePresenter{
    var details_vd: TextView;
    var mHandler: Handler
    var activity: AppCompatActivity
    var verticalDisplacementModule: VerticalDisplacementModule
    lateinit var vdRunnable: Runnable

    constructor(details_vd: TextView, activity: AppCompatActivity) {
        this.details_vd = details_vd
        this.activity = activity
        verticalDisplacementModule = VerticalDisplacementModule(activity)
        mHandler = Handler()
    }

    override fun start() {
        vdRunnable = object : Runnable {
            override fun run() {
                details_vd.setText(verticalDisplacementModule.VP_s64_GetCurrentVerticalPosition().toString())
                mHandler.postDelayed(this, 1000)
            }
        }

        verticalDisplacementModule.locationUpdateState = true
        verticalDisplacementModule.startLocationUpdates()
        mHandler.post(vdRunnable)
    }

    override fun stop() {
        mHandler.removeCallbacks(vdRunnable)
    }
}