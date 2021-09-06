package com.hossein.cryptopricewidget

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.hossein.cryptopricewidget.service.UpdateJobService
import java.util.*
import java.util.concurrent.TimeUnit
import com.hossein.cryptopricewidget.provider
import com.hossein.cryptopricewidget.provider.StringProvider


/**
 * Implementation of App Widget functionality.
 */
class BitcoinPriceWidget : AppWidgetProvider() {
    private var jobId: Int = 0

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        val scheduler= context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        scheduler.cancel(jobId)
    }
    private fun schduleUpdate(context: Context) {
        val componentName = ComponentName(context, UpdateJobService::class.java)
        jobId = Random().nextInt(100)
        val info = JobInfo.Builder(jobId, componentName)
            .setPeriodic(TimeUnit.MINUTES.toMillis(15))
            .setPersisted(true)
            .build()
        val scheduler= context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val result = scheduler.schedule(info)
        if (result == JobScheduler.RESULT_SUCCESS){
        }else{
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent?.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE){
            var price = if (intent.hasExtra(StringProvider.price)){
                intent.getStringExtra(StringProvider.price)
            }else{
                StringProvider.tryAgain
            }
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context!!.applicationContext, BitcoinPriceWidget::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            for (appWidgetId in allWidgetIds) {
                updateAppWidget(
                    context,
                    appWidgetManager,
                    appWidgetId,
                    price!!
                )
            }
        }
    }

}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    price: String
) {
    val widgetText = context.getString(R.string.appwidget_text)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.bitcoin_price_widget)
    views.setTextViewText(R.id.textView, widgetText)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}