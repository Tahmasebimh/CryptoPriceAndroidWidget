package com.hossein.cryptopricewidget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.hossein.cryptopricewidget.util.pref.PrefManager
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {
    private lateinit var map: HashMap<String, ArrayList<Double>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.txtMain)
        textView.text = PrefManager.getDailyPriceMap(context = this).toString()
    }

}