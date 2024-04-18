package io.woonex.stockBuddy.alpha

import com.google.gson.annotations.SerializedName

/** This is a class that pulls the data from the api for stock prices
 */
data class DailyAlphaData(
    @SerializedName("Time Series (Daily)")
    val dailyAlphaData: Map<String, StockPrice>
)
