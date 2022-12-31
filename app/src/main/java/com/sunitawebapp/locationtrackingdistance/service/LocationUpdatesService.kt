package com.sunitawebapp.locationtrackingdistance.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.sunitawebapp.locationtrackingdistance.AppController
import com.sunitawebapp.locationtrackingdistance.R
import com.sunitawebapp.locationtrackingdistance.livedata.LocationLiveData
import com.google.android.gms.maps.model.LatLng

var currentpoint : LatLng?=null
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
      /*  var getLocationData: LiveData<Location> = locationData
        getLocationData.observe(this, Observer {
            notifyText= getLocationText(it)!!
            startForeground(1, getNotification())
        })*/
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

            currentpoint= LatLng(it.latitude, it.longitude)


            var distanceresult= AppController.distanceCalculate(currentpoint)
          //  AppController.storedata(it,distanceresult)
            notifyText= getLocationText(it)!! + ", Distance :"+String.format("%.2f", distanceresult / 1000) + "km"
            startForeground(1, getNotification())
        })

    }
    fun getNotification() : Notification{


        createNotificationChannel()
        val builder = NotificationCompat.Builder(this,CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("location Tracking")
            .setContentText("$notifyText")

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
        try {
            Toast.makeText(this, "onDestroyservice", Toast.LENGTH_SHORT).show()
        }catch (e : Exception){
            Toast.makeText(this, "onDestroyservice", Toast.LENGTH_SHORT).show()
        }


    }

    

}
