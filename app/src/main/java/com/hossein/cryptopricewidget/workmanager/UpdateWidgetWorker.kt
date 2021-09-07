package com.hossein.cryptopricewidget.workmanager

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.hossein.cryptopricewidget.BitcoinPriceWidget

class UpdateWidgetWorker(val appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    val TAG = "UpdateWidgetWorker>>>"
    override fun doWork(): Result {
        val intent = Intent(appContext, BitcoinPriceWidget::class.java)
        intent.action = BitcoinPriceWidget.ACTION_UPDATE_MANUAL
        appContext.sendBroadcast(intent)
        return Result.success()
    }
}