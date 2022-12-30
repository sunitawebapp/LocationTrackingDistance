package com.example.locationtrackingdistance

import android.Manifest
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Intent
import android.os.Build
import com.example.locationtrackingdistance.service.LocationUpdatesService


class AppController : Application() {

    override fun onCreate() {
        super.onCreate()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, LocationUpdatesService::class.java))
        }



    }
}
