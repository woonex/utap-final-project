package io.woonex.stockBuddy.api

import com.google.gson.GsonBuilder
import io.woonex.stockBuddy.alpha.DailyAlphaData
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface AlphaApi {

//    @GET("/query?function=TIME_SERIES_WEEKLY&apikey=" + key)
//    suspend fun weeklyData(@Query("symbol") symbol: String) : WeeklyAlphaData

    @GET("/query?function=TIME_SERIES_DAILY&symbol=IBM&outputsize=full&apikey=demo")
    suspend fun dailyDataDemo() : DailyAlphaData

    @GET("/query?function=TIME_SERIES_DAILY&symbol=IBM&outputsize=full&apikey=" + key)
    suspend fun dailyData(@Query("symbol") symbol: String) : DailyAlphaData

    companion object {
        private const val key = "C4GO7JJ25MVWL5SR"

        var httpurl = HttpUrl.Builder()
            .scheme("https")
            .host("www.alphavantage.co")
            .build()

        fun create() : AlphaApi = create(httpurl)

        private fun buildGsonConverterFactory() : GsonConverterFactory {
            return GsonConverterFactory.create(GsonBuilder().create())
        }

        private fun create(httpUrl: HttpUrl) : AlphaApi {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    // Enable basic HTTP logging to help with debugging.
                    this.level = HttpLoggingInterceptor.Level.BASIC
                })
                .build()
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(buildGsonConverterFactory())
                .build()
                .create(AlphaApi::class.java)
        }
    }
}