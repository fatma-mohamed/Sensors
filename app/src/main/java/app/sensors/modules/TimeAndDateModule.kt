package app.sensors.modules

import java.util.*
import java.text.SimpleDateFormat


class TimeAndDateModule {

    /**
     * Returns current time in seconds
     * @return time in String
     */
    fun TND_str_GetCurrentTime(): String? {
        val calendar = Calendar.getInstance().time

        val df = SimpleDateFormat("HH:mm:ss")
        return df.format(calendar)
    }

    /**
     * Returns current date
     * @return date in String
     */
    fun TND_str_GetCurrentDate(): String? {
        val calendar = Calendar.getInstance().time

        val df = SimpleDateFormat("yyyy-MM-dd")
        return df.format(calendar)
    }

    /**
     * Returns current date and time in Gregorian format
     * @return date & time in String
     */
    fun TND_str_GetCurrentTimeAndDate(): String? {
        val calendar = Calendar.getInstance().time

        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return df.format(calendar)
    }
}
