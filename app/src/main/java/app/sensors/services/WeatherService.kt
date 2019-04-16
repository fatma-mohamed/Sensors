package app.sensors.services

import app.sensors.models.Model
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

interface WeatherService {

    @GET("weather")
    fun getWeatherByCityName(@Query("q") cityName: String,
                             @Query("appid") key: String)
            :Observable<Model.Weather>

    companion object {
        fun create(): WeatherService {

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create())
                .addConverterFactory(
                    GsonConverterFactory.create())
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .build()

            return retrofit.create(WeatherService::class.java)
        }
    }
}