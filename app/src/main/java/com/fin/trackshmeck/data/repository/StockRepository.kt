package com.fin.trackshmeck.data.repository

import com.fin.trackshmeck.data.model.WatchlistItem
import kotlinx.coroutines.flow.Flow

interface StockRepository {
    fun observeWatchlist(): Flow<List<WatchlistItem>>
    suspend fun addItem(ticker: String, quantity: Double = 0.0): Long
    suspend fun updateTicker(id: Long, newTicker: String): Boolean
    suspend fun updateQuantity(id: Long, newQuantity: Double)
    suspend fun deleteItem(id: Long)
    suspend fun refreshAll()
    suspend fun prepopulateIfEmpty()
}
