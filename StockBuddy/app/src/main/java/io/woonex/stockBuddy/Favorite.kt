package io.woonex.stockBuddy

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class Favorite(
    val ownerUid: String = "",
    var stockName: String = "",
    @ServerTimestamp val timeStamp: Timestamp? = null,
    @DocumentId var firestoreID: String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Favorite

        if (ownerUid != other.ownerUid) return false
        if (stockName != other.stockName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ownerUid.hashCode()
        result = 31 * result + stockName.hashCode()
        return result
    }


}

