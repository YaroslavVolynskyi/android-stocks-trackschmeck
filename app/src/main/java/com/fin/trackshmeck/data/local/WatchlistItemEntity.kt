package com.fin.trackshmeck.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist_items")
data class WatchlistItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ticker: String = "",
    val quantity: Double = 0.0,
    // Quote fields
    val currentPrice: Double = 0.0,
    val change: Double = 0.0,
    val percentChange: Double = 0.0,
    val highPrice: Double = 0.0,
    val lowPrice: Double = 0.0,
    val openPrice: Double = 0.0,
    val previousClose: Double = 0.0,
    // Company profile fields
    val companyName: String? = null,
    val country: String? = null,
    val currency: String? = null,
    val exchange: String? = null,
    val industry: String? = null,
    val ipoDate: String? = null,
    val logoUrl: String? = null,
    val marketCap: Double? = null,
    val phone: String? = null,
    val sharesOutstanding: Double? = null,
    val webUrl: String? = null,
    // Metadata
    val lastUpdated: Long = 0L,
    val fetchError: String? = null,
)
