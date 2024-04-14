package io.woonex.stockBuddy

import java.io.Serializable

data class Stock(
    val abbreviation:String,
    val name:String = "",
    val currentPrice:Float = 0f
    ): Serializable {

    fun searchFor(searchTerm: String): Boolean {
        if (searchTerm == "") {
            return true
        }

        return (name.contains(searchTerm, true)) || abbreviation.contains(searchTerm, true)
    }
}
