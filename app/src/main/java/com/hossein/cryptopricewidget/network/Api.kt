package com.hossein.cryptopricewidget.network

import com.hossein.cryptopricewidget.model.BitcoinPriceModel
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    @GET("tobtc")
    fun getPrice(
        @Query("currency") currency: String?,
        @Query("value") value: String?
    ): Single<ResponseBody>?

    @GET("ticker")
    fun getBitcoinPrice(
    ): Single<BitcoinPriceModel>
}