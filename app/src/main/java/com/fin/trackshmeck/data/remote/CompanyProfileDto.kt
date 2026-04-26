package com.fin.trackshmeck.data.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CompanyProfileDto(
    val name: String? = null,
    val country: String? = null,
    val currency: String? = null,
    val exchange: String? = null,
    val finnhubIndustry: String? = null,
    val ipo: String? = null,
    val logo: String? = null,
    val marketCapitalization: Double? = null,
    val phone: String? = null,
    val shareOutstanding: Double? = null,
    val ticker: String? = null,
    val weburl: String? = null,
)
