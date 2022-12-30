package com.example.locationtrackingdistance.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.locationtrackingdistance.R
import com.example.locationtrackingdistance.livedata.LocationLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationUpdatesService : LifecycleService(){
    val CHANNEL_ID = "ForegroundServiceChannel"
    var notifyText=""

    private var isGPSEnabled = false
    override fun onCreate() {
        super.onCreate()


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        var locationData =
            LocationLiveData(this)

        /**
         * LiveData a public field to observe the changes of location
         */
        var getLocationData: LiveData<Location> = locationData
        getLocationData.observe(this, Observer {
            notifyText= getLocationText(it)!!
            startForeground(1, getNotification())
        })
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)


        var locationData =
            LocationLiveData(this)

        /**
         * LiveData a public field to observe the changes of location
         */
        var getLocationData: LiveData<Location> = locationData
        getLocationData.observe(this, Observer {
            notifyText= getLocationText(it)!!
            startForeground(1, getNotification())
        })


    }
    fun getNotification() : Notification{
        createNotificationChannel()
        val builder = NotificationCompat.Builder(this,CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("location Tracking")
            .setContentText(System.currentTimeMillis().toString())

        // Add as notification
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, builder.build())
        return builder.build();
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }
    fun getLocationText(location: Location?): String? {
        return if (location == null) "Unknown location" else "(" + location.latitude + ", " + location.longitude + ")"
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this, "sreg", Toast.LENGTH_SHORT).show()
    }

}