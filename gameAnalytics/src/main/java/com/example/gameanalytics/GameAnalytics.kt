package com.example.gameanalytics

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import androidx.ads.identifier.AdvertisingIdClient
import androidx.ads.identifier.AdvertisingIdInfo
import androidx.annotation.RequiresApi
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures.addCallback
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.Writer
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


fun getInitEvent(context: Context): InitEvent{

    val a: Activity = context as Activity
    val displayMetrics = DisplayMetrics()
    @Suppress("DEPRECATION")
    a.windowManager.defaultDisplay.getMetrics(displayMetrics)

    val width = displayMetrics.widthPixels
    val height = displayMetrics.heightPixels

    var adId: String? ="00000000-0000-0000-0000-000000000000"
    var trackEnabled: Boolean? = false

    if (AdvertisingIdClient.isAdvertisingIdProviderAvailable(a)) {
        val advertisingIdInfoListenableFuture =
            AdvertisingIdClient.getAdvertisingIdInfo(a)

        addCallback(advertisingIdInfoListenableFuture,
            object : FutureCallback<AdvertisingIdInfo> {
                override fun onSuccess(adInfo: AdvertisingIdInfo?) {
                    adId = adInfo?.id
                    trackEnabled = adInfo?.isLimitAdTrackingEnabled
                }

                // Any exceptions thrown by getAdvertisingIdInfo()
                // cause this method to be called.
                override fun onFailure(t: Throwable) {
                    Log.e(
                        "MY_APP_TAG",
                        "Failed to connect to Advertising ID provider."
                    )
                    // Try to connect to the Advertising ID provider again or fall
                    // back to an ad solution that doesn't require using the
                    // Advertising ID library.
                }
            }, Executors.newSingleThreadExecutor()
        )
    }

    return (InitEvent(Build.VERSION.RELEASE, height, width, trackEnabled, adId, System.nanoTime()))
}

/*fun startEvents(init: InitEvent, session: SessionEvent, match: MatchEvent, freq: TimeFrequency, context: Context){

    val mainHandler = Handler(Looper.getMainLooper())

    mainHandler.post(object : Runnable {
        @RequiresApi(Build.VERSION_CODES.KITKAT)
        override fun run() {
            storeEvents(init, session, match, context)
            mainHandler.postDelayed(this, freq.getFrequency().toLong()*1000)
        }
    })
}*/

@RequiresApi(Build.VERSION_CODES.KITKAT)
fun storeEvents(init: InitEvent, session: SessionEvent, match: MatchEvent, context: Context, userId: String){

    val json = JSONObject()

    json.put("Init",
            JSONObject()
                .put("os_version", init.osVersion)
                .put("device_display_height", init.deviceDisplayHeight)
                .put("device_display_width", init.deviceDisplayWidth)
                .put("advertising_tracking_enabled", init.adTrackingEnabled)
                .put("advertising_id", init.adId)
                .put("event_age", TimeUnit.SECONDS.convert(System.nanoTime() - init.eventAge, TimeUnit.NANOSECONDS))
    )
    json.put("match",
        JSONObject()
            .put("result", match.result)
            .put("duration", match.duration)
            .put("event_age", TimeUnit.SECONDS.convert(System.nanoTime() - match.eventAge, TimeUnit.NANOSECONDS)))
    json.put("session",
        JSONObject()
            .put("duration", session.duration)
            .put("event_age", TimeUnit.SECONDS.convert(System.nanoTime() - session.eventAge, TimeUnit.NANOSECONDS)))

    saveJson(json.toString(),context, userId)
    Log.d("PEDRO", json.toString())

    return
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
fun saveJson(jsonString: String, context: Context, userId: String){

    val output: Writer
    val file = createFile(context,  userId)
    output = BufferedWriter(FileWriter(file))
    output.write(jsonString)
    output.close()
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
fun createFile(context: Context, userId: String): File {
    val fileName = "${userId}_" + java.sql.Timestamp(System.currentTimeMillis())

    Log.d("PEDRO", fileName)

    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

    if (storageDir != null) {
        if(!storageDir.exists()){
            storageDir.mkdir()
        }
    }

    return File.createTempFile(
            fileName,
            ".json",
            storageDir
    )
}


