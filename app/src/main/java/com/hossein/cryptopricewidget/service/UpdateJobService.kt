package com.hossein.cryptopricewidget.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import com.hossein.cryptopricewidget.BitcoinPriceWidget
import com.hossein.cryptopricewidget.api.CommonSignals
import com.hossein.cryptopricewidget.model.BitcoinPriceModel
import io.reactivex.observers.DisposableSingleObserver

class UpdateJobService: JobService() {
    override fun onStartJob(p0: JobParameters?): Boolean {
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
                intent.putExtra("price", data.map["USD"]?.last)
                sendBroadcast(intent)
            }

            override fun onError(e: Throwable) {
            }

        })
        return true
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return true
    }
}