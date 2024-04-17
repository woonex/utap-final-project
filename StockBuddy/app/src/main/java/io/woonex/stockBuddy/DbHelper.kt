package io.woonex.stockBuddy

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class DbHelper {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val rootCollection = "userData"

    private fun get(query: Query,
                            resultListener: (List<Favorite>)->Unit) {
        query
            .get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "userData fetch ${result!!.documents.size}")
                // NB: This is done on a background thread
                resultListener(result.documents.mapNotNull {
                    it.toObject(Favorite::class.java)
                })
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "userData fetch FAILED ", it)
                resultListener(listOf())
            }
    }

    fun fetchLiked(
        abbr: String,
        resultListener: (List<Favorite>) -> Unit
    ) {
        val query = db.collection(rootCollection).whereEqualTo("stockName", abbr)
        get(query, resultListener)
    }

    fun fetchFavorites(
        ownerUid: String,
        resultListener: (List<Favorite>) -> Unit
    ) {
        if (ownerUid == invalidUserUid) {
            return
        }
        val query = db.collection(rootCollection).whereEqualTo("ownerUid", ownerUid)
        get(query, resultListener)
    }

    fun addFavorite(
        favorite: Favorite,
        successListener: () -> Unit,
        failureListener: () -> Unit
    ) {
        db.collection(rootCollection).add(favorite)
            .addOnSuccessListener {
                successListener()
            }
            .addOnFailureListener {
                failureListener()
            }
    }

    fun removeFavorite(
        favorite: Favorite,
        successListener: () -> Unit,
        failureListener: () -> Unit
    ) {
        Log.d("DbHelper", favorite.firestoreID)
        db.collection(rootCollection).document(favorite.firestoreID)
            .delete()
            .addOnSuccessListener {
                successListener()
            }
            .addOnFailureListener {
                failureListener()
            }
    }
}