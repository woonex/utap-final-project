package io.woonex.stockBuddy.alpha

import com.google.gson.annotations.SerializedName

data class StockPrice(
    @SerializedName("1. open")
    val openPrice : String,
    @SerializedName("2. high")
    val highPrice : String,
    @SerializedName("3. low")
    val lowPrice : String,
    @SerializedName("4. close")
    val closePrice : String,
    @SerializedName("5. volume")
    val volume: String
)
