package io.woonex.stockBuddy

import io.woonex.stockBuddy.alpha.TimeData
import java.io.Serializable

/** the backbone of what is being displayed on the data
 *
 */
data class Stock(
    val abbreviation:String,
    val name:String = "",
    val currentPrice:Float? = 0f,
    val open : Float? = null,
    val low : Float? = null,
    val high: Float? = null,
    val change : Float? = null,
    val favorite: Boolean = false,
    val historicalData: List<TimeData>? = null
    ): Serializable {

    fun searchFor(searchTerm: String): Boolean {
        if (searchTerm == "") {
            return true
        }

        return (name.contains(searchTerm, true)) || abbreviation.contains(searchTerm, true)
    }

    override fun toString(): String {
        return abbreviation
    }
}
