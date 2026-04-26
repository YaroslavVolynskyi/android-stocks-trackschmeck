package com.fin.trackshmeck.data.model

import com.fin.trackshmeck.data.local.WatchlistItemEntity

fun WatchlistItemEntity.toWatchlistItem(): WatchlistItem = WatchlistItem(
    id = id,
    ticker = ticker,
    quantity = quantity,
    currentPrice = currentPrice,
    change = change,
    percentChange = percentChange,
    highPrice = highPrice,
    lowPrice = lowPrice,
    openPrice = openPrice,
    previousClose = previousClose,
    companyName = companyName,
    country = country,
    currency = currency,
    exchange = exchange,
    industry = industry,
    ipoDate = ipoDate,
    logoUrl = logoUrl,
    marketCap = marketCap,
    phone = phone,
    sharesOutstanding = sharesOutstanding,
    webUrl = webUrl,
    fetchError = fetchError,
)
