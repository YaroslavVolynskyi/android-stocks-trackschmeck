package com.fin.trackshmeck.data.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseDataSource @Inject constructor() {

    private val database = FirebaseDatabase.getInstance("https://tickers-shmickers.firebaseio.com/")
    private val auth = FirebaseAuth.getInstance()

    suspend fun fetchTickers(): List<FirebaseTicker> {
        if (auth.currentUser == null) {
            auth.signInAnonymously().await()
        }

        val snapshot = database.reference.get().await()
        val tickers = mutableListOf<FirebaseTicker>()

        for (child in snapshot.children) {
            val ticker = child.key?.uppercase() ?: continue
            val quantityStr = (child.value as? Map<*, *>)?.get("quantity") as? String ?: "0"
            val quantity = quantityStr.replace(",", ".").toDoubleOrNull() ?: 0.0
            tickers.add(FirebaseTicker(ticker = ticker, quantity = quantity))
        }
        return tickers
    }
}

data class FirebaseTicker(
    val ticker: String,
    val quantity: Double = 0.0,
)
