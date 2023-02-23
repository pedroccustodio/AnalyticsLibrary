package com.example.analyticslibrary

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.gameanalytics.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private var initEvent = InitEvent("", 0, 0, false, "", 0)
    private var sessionEvent = SessionEvent(0,0)
    private var matchEvent = MatchEvent("", 0, 0)

    private val user = User()
    private val timeFrequency = TimeFrequency()

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        initEvent = getInitEvent(this@MainActivity)

        user.setUserId("Pedro")
        timeFrequency.setFrequency(10)

        //storeEvents(initEvent, sessionEvent, matchEvent, this@MainActivity)

        val mainHandler = Handler(Looper.getMainLooper())

        mainHandler.post(object : Runnable {
            @RequiresApi(Build.VERSION_CODES.KITKAT)
            override fun run() {
                storeEvents(initEvent, sessionEvent, matchEvent, this@MainActivity, user.getUserId())
                mainHandler.postDelayed(this, timeFrequency.getFrequency().toLong()*1000)
            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onStart() {
        super.onStart()

        initEvent = getInitEvent(this@MainActivity)

        storeEvents(initEvent, sessionEvent, matchEvent, this@MainActivity, user.getUserId())

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onPause() {
        super.onPause()

        sessionEvent = SessionEvent(TimeUnit.SECONDS.convert(System.nanoTime() - initEvent.eventAge, TimeUnit.NANOSECONDS).toInt(), System.nanoTime())

        storeEvents(initEvent, sessionEvent, matchEvent, this@MainActivity, user.getUserId())
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onDestroy() {
        super.onDestroy()

        sessionEvent = SessionEvent(TimeUnit.SECONDS.convert(System.nanoTime() - initEvent.eventAge, TimeUnit.NANOSECONDS).toInt(), System.nanoTime())

        storeEvents(initEvent, sessionEvent, matchEvent, this@MainActivity, user.getUserId())
    }


}