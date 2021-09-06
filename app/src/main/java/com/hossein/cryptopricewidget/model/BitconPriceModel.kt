package com.hossein.cryptopricewidget.model

data class BitcoinPriceModel(
    val map: HashMap<String, Currency>
)

data class Currency(
    val `15m`: Double,
    val buy: Double,
    val last: Double,
    val sell: Double,
    val symbol: String
)
