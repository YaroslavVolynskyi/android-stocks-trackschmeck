package com.fin.trackshmeck.data.datasource

import com.fin.trackshmeck.data.remote.CompanyProfileDto
import com.fin.trackshmeck.data.remote.FinnhubApi
import com.fin.trackshmeck.data.remote.QuoteDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class StockRemoteDataSource @Inject constructor(
    private val api: FinnhubApi,
    @param:Named("apiKey") private val apiKey: String,
) {
    suspend fun getQuote(symbol: String): QuoteDto = withContext(Dispatchers.IO) {
        api.getQuote(symbol, apiKey)
    }

    suspend fun getCompanyProfile(symbol: String): CompanyProfileDto = withContext(Dispatchers.IO) {
        api.getCompanyProfile(symbol, apiKey)
    }
}
