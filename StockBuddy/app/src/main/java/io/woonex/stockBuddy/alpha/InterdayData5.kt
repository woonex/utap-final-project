package io.woonex.stockBuddy.alpha

import com.google.gson.annotations.SerializedName

data class InterdayData5(
    @SerializedName("Time Series (5min)")
    val interday5: Map<String, StockPrice>
)
