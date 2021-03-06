package com.hossein.cryptopricewidget.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.util.Log
import com.hossein.cryptopricewidget.BitcoinPriceWidget
import com.hossein.cryptopricewidget.network.CommonSignals
import com.hossein.cryptopricewidget.model.BitcoinPriceModel
import com.hossein.cryptopricewidget.util.provider.StringProvider
import com.hossein.cryptopricewidget.util.pref.PrefManager
import io.reactivex.observers.DisposableSingleObserver

class UpdateJobService: JobService() {
    val TAG = "UpdateJobService>>>"
    override fun onStartJob(p0: JobParameters?): Boolean {
        Log.d(TAG, "onStartJob: ")
        val disposable = CommonSignals.instance.getBitcoinPrice().subscribeWith(object :
            DisposableSingleObserver<BitcoinPriceModel>() {
            override fun onSuccess(data: BitcoinPriceModel) {
                val intent = Intent(this@UpdateJobService, BitcoinPriceWidget::class.java)
                intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                val ids = AppWidgetManager.getInstance(application).getAppWidgetIds(
                    ComponentName(
                        applicationContext,
                        BitcoinPriceWidget::class.java
                    )
                )
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                intent.putExtra(StringProvider.price, data["USD"]?.last.toString() + " " + data["USD"]?.symbol)
                sendBroadcast(intent)
                PrefManager.savePrice(data["USD"]?.last.toString() + " " + data["USD"]?.symbol, this@UpdateJobService)
                PrefManager.addToDailyPriceList(
                    context = this@UpdateJobService,
                    value = data["USD"]!!.last
                )
                jobFinished(p0, true)
            }

            override fun onError(e: Throwable) {
                jobFinished(p0, true)
            }

        })
        return true
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        Log.d(TAG, "onStopJob: ")
        return true
    }
}