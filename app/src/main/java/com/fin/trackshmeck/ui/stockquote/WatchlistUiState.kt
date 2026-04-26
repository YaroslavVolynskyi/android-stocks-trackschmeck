package com.fin.trackshmeck.ui.stockquote

data class WatchlistUiState(
    val items: List<WatchlistItemUi> = emptyList(),
    val isLoading: Boolean = true,
    val focusItemId: Long? = null,
)

data class WatchlistItemUi(
    val id: Long,
    val ticker: String,
    val price: Double,
    val quantity: Double,
    val value: Double,
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
    val isExpanded: Boolean,
    val fetchError: String?,
)
