package com.hossein.cryptopricewidget.workmanager

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.hossein.cryptopricewidget.BitcoinPriceWidget
import com.hossein.cryptopricewidget.api.CommonSignals
import com.hossein.cryptopricewidget.model.BitcoinPriceModel
import com.hossein.cryptopricewidget.util.pref.PrefManager
import com.hossein.cryptopricewidget.util.provider.StringProvider
import io.reactivex.observers.DisposableSingleObserver
import okhttp3.internal.wait
import java.util.concurrent.TimeUnit

class UpdateWidgetWorker(val appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    val TAG = "UpdateWidgetWorker>>>"
    override fun doWork(): Result {
        Log.d(TAG, "doWork: 000")
        val intent = Intent(appContext, BitcoinPriceWidget::class.java)
        intent.action = BitcoinPriceWidget.ACTION_UPDATE_MANUAL
        appContext.sendBroadcast(intent)
        Log.d(TAG, "doWork: 111")
        return Result.success()
    }
}