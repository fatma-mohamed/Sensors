package app.sensors.models


object Model {
    data class Weather(val main: Main)
    data class Main(val temp: Float)
}