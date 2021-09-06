package com.hossein.cryptopricewidget.model

class BitcoinPriceModel :HashMap<String, Currency>()

data class Currency(
    val `15m`: Double,
    val buy: Double,
    val last: Double,
    val sell: Double,
    val symbol: String
)
