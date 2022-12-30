package com.example.locationtrackingdistance

import android.Manifest
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Intent
import android.os.Build
import com.example.locationtrackingdistance.service.LocationUpdatesService
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class AppController : Application() {

    override fun onCreate() {
        super.onCreate()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, LocationUpdatesService::class.java))
        }



    }
}