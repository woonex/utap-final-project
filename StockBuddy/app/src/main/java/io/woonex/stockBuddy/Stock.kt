package io.woonex.stockBuddy

import java.io.Serializable

data class Stock(
    val name:String,
    val abbreviation:String
    ): Serializable {

    fun searchFor(searchTerm: String): Boolean {
        if (searchTerm == "") {
            return true
        }

        return name.contains(searchTerm, true) || abbreviation.contains(searchTerm, true)
    }
}
