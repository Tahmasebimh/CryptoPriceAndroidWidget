package com.hossein.cryptopricewidget.util.pref

import android.content.Context

object PrefManager {

    private val KEY_PRICE: String = "PriceKey"
    private val MAIN_PREF: String = "AppPreferences"

    fun savePrice(price: String, context: Context){
        val  settings = context.getSharedPreferences(
            MAIN_PREF, Context.MODE_PRIVATE
        )
        settings.edit().putString(KEY_PRICE, price).apply()
    }
    fun getPrice(context: Context): String?{
        val  settings = context.getSharedPreferences(
            MAIN_PREF, Context.MODE_PRIVATE
        )
        return settings.getString(KEY_PRICE, null)
    }

}