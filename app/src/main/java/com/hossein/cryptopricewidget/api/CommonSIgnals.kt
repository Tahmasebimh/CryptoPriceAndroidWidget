package com.hossein.cryptopricewidget.api

import com.hossein.cryptopricewidget.model.BitcoinPriceModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody

class CommonSignals private constructor() {
    private val api: Api = RetrofitClientInstance.getRetrofit().create(
        Api::class.java
    )

    private fun <T> process(source: Single<T>?): Single<T> {
        return source!!.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun getPrice(currency: String?, value: String?): Single<ResponseBody> {
        return process(api.getPrice(currency, value))
    }
    fun getBitcoinPrice(): Single<BitcoinPriceModel>{
        return process(api.getBitcoinPrice())
    }

    companion object {
        val instance = CommonSignals()
    }

}