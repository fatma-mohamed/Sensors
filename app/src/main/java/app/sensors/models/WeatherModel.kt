package app.sensors.models

/**
 * OpenWeather API data model
 */
object Model {
    data class Weather(val main: Main)
    data class Main(val temp: Float)
}