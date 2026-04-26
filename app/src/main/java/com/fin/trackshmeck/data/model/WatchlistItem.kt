package com.fin.trackshmeck.data.model

data class WatchlistItem(
    val id: Long,
    val ticker: String,
    val quantity: Double,
    val currentPrice: Double,
    val change: Double,
    val percentChange: Double,
    val highPrice: Double,
    val lowPrice: Double,
    val openPrice: Double,
    val previousClose: Double,
    val companyName: String?,
    val country: String?,
    val currency: String?,
    val exchange: String?,
    val industry: String?,
    val ipoDate: String?,
    val logoUrl: String?,
    val marketCap: Double?,
    val phone: String?,
    val sharesOutstanding: Double?,
    val webUrl: String?,
    val fetchError: String?,
) {
    val value: Double get() = currentPrice * quantity
}
