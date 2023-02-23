package com.example.gameanalytics

data class InitEvent(val osVersion: String, val deviceDisplayHeight: Int, val deviceDisplayWidth: Int, var adTrackingEnabled: Boolean?, val adId: String?, var eventAge: Long)
