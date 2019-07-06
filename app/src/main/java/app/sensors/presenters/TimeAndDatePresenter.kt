package app.sensors.presenters

import android.os.Handler
import android.widget.TextView
import app.sensors.modules.TimeAndDateModule

class TimeAndDatePresenter : Presenter {
    override var started: Boolean
    lateinit var tnd_current_time: TextView
    lateinit var tnd_current_date: TextView
    lateinit var tnd_current_time_and_date: TextView
    lateinit var mHandler: Handler
    var timeAndDateModule: TimeAndDateModule
    lateinit var timeRunnable: Runnable
    lateinit var dateRunnable: Runnable
    lateinit var timeAndDateRunnable: Runnable

    constructor(tnd_current_time: TextView, tnd_current_date: TextView, tnd_current_time_and_date: TextView) {
        started = false
        this.tnd_current_time = tnd_current_time
        this.tnd_current_date = tnd_current_date
        this.tnd_current_time_and_date = tnd_current_time_and_date
        this.timeAndDateModule = TimeAndDateModule()
        mHandler = Handler()
    }

    /**
     * Start task running every second to update location
     */
    override fun start() {
        started = true
        timeRunnable = object : Runnable {
            override fun run() {
                tnd_current_time.setText(timeAndDateModule.TND_str_GetCurrentTime())
                mHandler.postDelayed(this, 1000)
            }
        }

        dateRunnable = object : Runnable {
            override fun run() {
                tnd_current_date.setText(timeAndDateModule.TND_str_GetCurrentDate())
                mHandler.postDelayed(this, 86400000)
            }
        }

        timeAndDateRunnable = object : Runnable {
            override fun run() {
                tnd_current_time_and_date.setText(timeAndDateModule.TND_str_GetCurrentTimeAndDate())
                mHandler.postDelayed(this, 1000)
            }
        }

        mHandler.post(timeRunnable)
        mHandler.post(dateRunnable)
        mHandler.post(timeAndDateRunnable)
    }

    /**
     * Stop runnable task
     */
    override fun stop() {
        started = false
        mHandler.removeCallbacks(timeAndDateRunnable)
        mHandler.removeCallbacks(dateRunnable)
        mHandler.removeCallbacks(timeAndDateRunnable)
    }
}