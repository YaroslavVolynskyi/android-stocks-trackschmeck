package com.fin.trackshmeck.data.repository

import android.util.Log
import com.fin.trackshmeck.data.datasource.FirebaseDataSource
import com.fin.trackshmeck.data.datasource.StockRemoteDataSource
import com.fin.trackshmeck.data.local.WatchlistDao
import com.fin.trackshmeck.data.local.WatchlistItemEntity
import com.fin.trackshmeck.data.model.WatchlistItem
import com.fin.trackshmeck.data.model.toWatchlistItem
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StockRepositoryImpl @Inject constructor(
    private val dao: WatchlistDao,
    private val remoteDataSource: StockRemoteDataSource,
    private val firebaseDataSource: FirebaseDataSource,
) : StockRepository {

    override fun observeWatchlist(): Flow<List<WatchlistItem>> =
        dao.observeAll().map { entities -> entities.map { it.toWatchlistItem() } }

    override suspend fun addItem(ticker: String, quantity: Double): Long =
        dao.insert(WatchlistItemEntity(ticker = ticker, quantity = quantity))

    override suspend fun updateTicker(id: Long, newTicker: String): Boolean {
        val entity = dao.getById(id) ?: return false
        val trimmed = newTicker.uppercase().trim()
        if (trimmed.isEmpty()) {
            dao.update(entity.copy(ticker = trimmed, fetchError = null))
            return true
        }
        return try {
            val quote = remoteDataSource.getQuote(trimmed)
            val profile = remoteDataSource.getCompanyProfile(trimmed)
            if (quote.c == 0.0 && profile.name == null) {
                dao.update(entity.copy(ticker = trimmed, fetchError = "incorrect ticker"))
                false
            } else {
                dao.update(
                    entity.copy(
                        ticker = trimmed,
                        currentPrice = quote.c,
                        change = quote.d,
                        percentChange = quote.dp,
                        highPrice = quote.h,
                        lowPrice = quote.l,
                        openPrice = quote.o,
                        previousClose = quote.pc,
                        companyName = profile.name,
                        country = profile.country,
                        currency = profile.currency,
                        exchange = profile.exchange,
                        industry = profile.finnhubIndustry,
                        ipoDate = profile.ipo,
                        logoUrl = profile.logo,
                        marketCap = profile.marketCapitalization,
                        phone = profile.phone,
                        sharesOutstanding = profile.shareOutstanding,
                        webUrl = profile.weburl,
                        lastUpdated = System.currentTimeMillis(),
                        fetchError = null,
                    )
                )
                true
            }
        } catch (e: Exception) {
            dao.update(entity.copy(ticker = trimmed, fetchError = "incorrect ticker"))
            false
        }
    }

    override suspend fun updateQuantity(id: Long, newQuantity: Double) {
        val entity = dao.getById(id) ?: return
        dao.update(entity.copy(quantity = newQuantity))
    }

    override suspend fun deleteItem(id: Long) {
        dao.deleteById(id)
    }

    override suspend fun refreshAll() = coroutineScope {
        val items = dao.getAll()
        for (entity in items) {
            if (entity.ticker.isBlank()) continue
            launch {
                try {
                    val quote = remoteDataSource.getQuote(entity.ticker)
                    val profile = remoteDataSource.getCompanyProfile(entity.ticker)
                    dao.update(
                        entity.copy(
                            currentPrice = quote.c,
                            change = quote.d,
                            percentChange = quote.dp,
                            highPrice = quote.h,
                            lowPrice = quote.l,
                            openPrice = quote.o,
                            previousClose = quote.pc,
                            companyName = profile.name,
                            country = profile.country,
                            currency = profile.currency,
                            exchange = profile.exchange,
                            industry = profile.finnhubIndustry,
                            ipoDate = profile.ipo,
                            logoUrl = profile.logo,
                            marketCap = profile.marketCapitalization,
                            phone = profile.phone,
                            sharesOutstanding = profile.shareOutstanding,
                            webUrl = profile.weburl,
                            lastUpdated = System.currentTimeMillis(),
                            fetchError = null,
                        )
                    )
                } catch (e: Exception) {
                    dao.update(entity.copy(fetchError = e.message))
                }
            }
        }
    }

    override suspend fun prepopulateIfEmpty() {
        if (dao.count() > 0) return
        try {
            val tickers = firebaseDataSource.fetchTickers()
            Log.d("StockRepo", "Fetched ${tickers.size} tickers from Firebase")
            for (item in tickers) {
                dao.insert(WatchlistItemEntity(ticker = item.ticker, quantity = item.quantity))
            }
        } catch (e: Exception) {
            Log.e("StockRepo", "Firebase fetch failed, using defaults", e)
            val defaults = listOf(
                "VOO", "GLD", "NVDA", "AMZN", "JPM", "PPA",
                "XLE", "XLU", "VXUS", "AVGO", "IXC", "FIG", "SGOV",
            )
            for (ticker in defaults) {
                dao.insert(WatchlistItemEntity(ticker = ticker))
            }
        }
    }
}
