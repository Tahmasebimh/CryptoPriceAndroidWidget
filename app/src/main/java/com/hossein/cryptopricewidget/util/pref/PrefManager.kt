package com.hossein.cryptopricewidget.util.pref

import android.content.Context
import android.icu.number.IntegerWidth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object PrefManager {

    private val KEY_PRICE: String = "PriceKey"
    private val MAIN_PREF: String = "AppPreferences"
    private val DAILY_MAP: String = "DailyHashMap"

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

    fun addToDailyPriceList(context: Context, value: Double){
        val rightNow = Calendar.getInstance()
        val year =
            rightNow[Calendar.YEAR]
        val month =
            rightNow[Calendar.MONTH] + 1
        val dayInMonth =
            rightNow[Calendar.DAY_OF_MONTH]
        val date = year * 10000 + month * 100 + dayInMonth

        val map = getDailyPriceMap(context)
        if (map[date].isNullOrEmpty()){
            map[date] = ArrayList()
        }
        map[date]?.add(value)
        val  settings = context.getSharedPreferences(
            MAIN_PREF, Context.MODE_PRIVATE
        )
        settings.edit().putString(DAILY_MAP, Gson().toJson(map)).apply()
    }

    fun getDailyPriceMap(context: Context): HashMap<Int, ArrayList<Double>> {
        val  settings = context.getSharedPreferences(
            MAIN_PREF, Context.MODE_PRIVATE
        )
        val storedHashMapString = settings.getString(DAILY_MAP, HashMap<Int, ArrayList<Double>>().toString());
        val mapType: Type = object : TypeToken<HashMap<Int, ArrayList<Double>>>() {}.type
        return Gson().fromJson(storedHashMapString, mapType)
    }

}