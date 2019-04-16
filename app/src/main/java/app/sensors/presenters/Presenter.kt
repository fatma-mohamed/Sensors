package app.sensors.presenters

interface Presenter {
    var started: Boolean

    fun start() {}
    fun stop() {}
}