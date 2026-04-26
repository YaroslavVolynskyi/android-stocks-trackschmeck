package com.fin.trackshmeck.data.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QuoteDto(
    val c: Double = 0.0,
    val d: Double = 0.0,
    val dp: Double = 0.0,
    val h: Double = 0.0,
    val l: Double = 0.0,
    val o: Double = 0.0,
    val pc: Double = 0.0,
)
