package io.woonex.stockBuddy

import com.google.gson.annotations.SerializedName

data class Quote (
    @SerializedName("c")
    val currentPrice : Float,
    @SerializedName("h")
    val highPrice: Float,
    @SerializedName("l")
    val lowPrice:Float,
    @SerializedName("o")
    val openPrice:Float,
    @SerializedName("pc")
    val previousClosePrice:Float
    ) {

}
