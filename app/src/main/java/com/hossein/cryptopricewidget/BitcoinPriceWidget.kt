package com.hossein.cryptopricewidget

import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.hossein.cryptopricewidget.api.CommonSignals
import com.hossein.cryptopricewidget.model.BitcoinPriceModel
import com.hossein.cryptopricewidget.service.UpdateJobService
import com.hossein.cryptopricewidget.util.pref.PrefManager
import com.hossein.cryptopricewidget.util.provider.StringProvider
import io.reactivex.observers.DisposableSingleObserver
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Implementation of App Widget functionality.
 */
class BitcoinPriceWidget : AppWidgetProvider() {
    private var jobId: Int = 0
    val TAG = "BitcoinPriceWidget>>>"

    companion object{
        const val ACTION_UPDATE_MANUAL = "action.update.manual"
    }
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(
                context,
                appWidgetManager,
                appWidgetId,
                if (PrefManager.getPrice(context).isNullOrEmpty()) StringProvider.updating else PrefManager.getPrice(context)!!
            )
            updateManual(context)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        //Start job scheduler service to update widget data every 15 min
        scheduleUpdate(context)
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        val scheduler= context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        scheduler.cancel(jobId)
    }

    private fun scheduleUpdate(context: Context) {
        val componentName = ComponentName(context, UpdateJobService::class.java)
        jobId = Random().nextInt(100)
        val info = JobInfo.Builder(jobId, componentName)
            .setPeriodic(TimeUnit.MINUTES.toMillis(15))
            .setPersisted(true)
            .build()
        val scheduler= context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val result = scheduler.schedule(info)
        if (result == JobScheduler.RESULT_SUCCESS){
            Log.d(TAG, "scheduleUpdate: jobStartedSucess")
        }else{
            Log.d(TAG, "scheduleUpdate: job failed ")
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        Log.d(TAG, "onReceive: " + intent?.action)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val thisWidget = ComponentName(context!!.applicationContext, BitcoinPriceWidget::class.java)
        val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
        if (intent?.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE){
            val price = if (intent.hasExtra(StringProvider.price)){
                intent.getStringExtra(StringProvider.price)
            }else{
                StringProvider.updating
            }
            for (appWidgetId in allWidgetIds) {
                updateAppWidget(
                    context,
                    appWidgetManager,
                    appWidgetId,
                    price!!
                )
            }
        }else if(intent?.action == ACTION_UPDATE_MANUAL){
            updateManual(context)
        }
    }

    private fun updateManual(context: Context?) {
        val disposable = CommonSignals.instance.getBitcoinPrice().subscribeWith(object :
            DisposableSingleObserver<BitcoinPriceModel>() {
            override fun onSuccess(data: BitcoinPriceModel) {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val thisWidget = ComponentName(context!!.applicationContext, BitcoinPriceWidget::class.java)
                val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
                for (appWidgetId in allWidgetIds) {
                    updateAppWidget(
                        context,
                        appWidgetManager,
                        appWidgetId,
                        data["USD"]?.last.toString() + " " + data["USD"]?.symbol
                    )
                }
                PrefManager.savePrice(data["USD"]?.last.toString() + " " + data["USD"]?.symbol, context)
            }

            override fun onError(e: Throwable) {

            }

        })
    }

}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    price: String
) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.bitcoin_price_widget)
    views.setTextViewText(R.id.textView, price)
    views.setTextViewText(R.id.txtUpdate, StringProvider.latesUpdateAt + getCurrentTime())
    val intent = Intent(context, BitcoinPriceWidget::class.java)
    intent.action = BitcoinPriceWidget.ACTION_UPDATE_MANUAL
    val pi = PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    views.setOnClickPendingIntent(R.id.txtUpdate, pi)
    views.setInt(R.id.mainLayout, "setBackgroundResource", R.drawable.backgorund_curve_shape)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

fun getCurrentTime(): String {
    val rightNow = Calendar.getInstance()

    val dayInMonth =
        rightNow[Calendar.DAY_OF_MONTH]
    val month =
        rightNow[Calendar.MONTH] + 1
    val hour =
        rightNow[Calendar.HOUR_OF_DAY] // return the hour in 24 hrs format (ranging from 0-23)
    val minute =
        rightNow[Calendar.MINUTE]

    val formattedDay = if (dayInMonth < 10){
        "0$dayInMonth"
    }else{
        dayInMonth
    }
    val formattedMonth = if (month < 10){
        "0$month"
    }else{
        month
    }

    val formattedHour = if (hour < 10){
        "0$hour"
    }else{
        hour
    }

    val formattedMinute = if (minute < 10){
        "0$minute"
    }else{
        minute
    }

    return "$formattedDay/$formattedMonth $formattedHour:$formattedMinute"


}
