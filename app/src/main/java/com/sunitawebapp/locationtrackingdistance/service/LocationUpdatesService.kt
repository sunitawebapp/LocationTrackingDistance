package com.sunitawebapp.locationtrackingdistance.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.gms.maps.model.CircleOptions
import com.sunitawebapp.locationtrackingdistance.AppController
import com.sunitawebapp.locationtrackingdistance.R
import com.sunitawebapp.locationtrackingdistance.livedata.LocationLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.SphericalUtil
import com.sunitawebapp.locationtrackingdistance.AppController.Companion.checkConnection

import com.sunitawebapp.locationtrackingdistance.AppController.Companion.stringToLatLong
import com.sunitawebapp.locationtrackingdistance.MainActivity

var currentpoint : LatLng?=null
var existLongitude: String? = null
var existLatitude: String? = null

var previouspoint: LatLng? = null
class LocationUpdatesService : LifecycleService(){
    val CHANNEL_ID = "ForegroundServiceChannel"
    var notifyText=""

    var isGPSEnabled = false
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

    @SuppressLint("SuspiciousIndentation")
    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
     //  AppController.countTimer()

        var locationData =
            LocationLiveData(this)

        /**
         * LiveData a public field to observe the changes of location
         */
        var getLocationData: LiveData<Location> = locationData
        getLocationData.observe(this, Observer {

            currentpoint= LatLng(it.latitude, it.longitude)


            var distanceresult= AppController.distanceCalculate(currentpoint)



       /*     if (existLatitude != null && existLongitude != null) {
                val selected_location = Location("locationA")
                selected_location.latitude = existLatitude!!.toDouble()
                selected_location.longitude = existLongitude!!.toDouble()
                val near_locations = Location("locationB")
                near_locations.latitude = it!!.latitude
                near_locations.longitude = it!!.longitude
                val distance = selected_location.distanceTo(near_locations)
                Toast.makeText(this, distance.toString(), Toast.LENGTH_SHORT).show()
                if (distance > 70.0) {
                    Toast.makeText(this, "You are outside $distance", Toast.LENGTH_SHORT).show()
                    notifyText= getLocationText(it)!! + ", Distance :"+String.format("%.2f", distance / 1000)  + "km"
                }else{
                    existLatitude=it.latitude.toString()
                    existLongitude= it.longitude.toString()
                }
            }else{
                existLatitude=it.latitude.toString()
                existLongitude= it.longitude.toString()
                notifyText= getLocationText(it)!! + ", Distance : 0.00km"
            }*/
            if (previouspoint == null) {
                countTimer(this,it)
              //  countTimerr()
            }


           /* if (checkConnection(this)){
                if (previouspoint==null){
                    previouspoint=LatLng(it!!.latitude,it!!.longitude)
                    notifyText= stringToLatLong("${it.latitude.toString()},${it.longitude.toString()}",this) + ", Distance : 0.00km"
                }else{
                    var distance= AppController.distanceCalculatewithForgroundservice( LatLng(previouspoint!!.latitude, previouspoint!!.longitude) ,LatLng(it!!.latitude,it!!.longitude))
                    Toast.makeText(this, distance.toString(), Toast.LENGTH_SHORT).show()
                    if (String.format("%.2f",  SphericalUtil.computeDistanceBetween(LatLng(previouspoint!!.latitude, previouspoint!!.longitude) ,LatLng(it!!.latitude,it!!.longitude)) / 1000).toDouble()  > 0.15.toString().toDouble()) {
                        Toast.makeText(this, "You are outside $distance", Toast.LENGTH_SHORT).show()

                        notifyText=  stringToLatLong("${it.latitude.toString()},${it.longitude.toString()}",this)+ ", Distance :"+String.format("%.2f", distance / 1000)  + "km"
                        previouspoint=LatLng(it!!.latitude,it!!.longitude)
                    }else{
                        previouspoint=LatLng(it!!.latitude,it!!.longitude)
                    }
                }
                startForeground(1, getNotification())
            }else{
                // initialize snack bar
                Toast.makeText(this, "Not Connected to Internet", Toast.LENGTH_SHORT).show()

            }*/




            //  AppController.storedata(it,distanceresult)
         // notifyText= getLocationText(it)!! + ", Distance :"+String.format("%.2f", distanceresult / 1000) + "km"

           // notifyText= getLocationText(it)!! + ", Distance :"+String.format("%.2f", distanceresult / 1000) + "km"+ ", new Distance :"+String.format("%.2f", distanceresult / 1000) + "km"

           // startForeground(1, getNotification())
           // startForeground(1, getNotification())
        })

    }
    fun getNotification() : Notification{


        createNotificationChannel()
        val builder = NotificationCompat.Builder(this,CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("location Tracking")
            .setContentText("$notifyText")
            .setStyle(NotificationCompat.BigTextStyle().bigText(notifyText).setSummaryText("count time"))
            .setNotificationSilent()

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

    fun countTimer(context: Context,it:Location) {
        object : CountDownTimer(300000, 1000) {

            // Callback function, fired on regular interval
            override fun onTick(millisUntilFinished: Long) {
                //    textView.setText("seconds remaining: " + millisUntilFinished / 1000)
                Log.d("timeing", "onTick: seconds remaining"+millisUntilFinished / 1000)
                if (checkConnection(context)) {
                    if (previouspoint == null) {
                        previouspoint = LatLng(it!!.latitude, it!!.longitude)
                        notifyText = stringToLatLong(
                            "${it.latitude.toString()},${it.longitude.toString()}",
                            context
                        ) + ", Distance : 0.00km"
                        startForeground(1, getNotification())
                    }

                }

            }

            // Callback function, fired
            // when the time is up
            override fun onFinish() {
                if (checkConnection(context)){
                /*    if (previouspoint==null){
                        previouspoint=LatLng(it!!.latitude,it!!.longitude)
                        notifyText= stringToLatLong("${it.latitude.toString()},${it.longitude.toString()}",context) + ", Distance : 0.00km"
                    }else{*/
                        var distance= AppController.distanceCalculatewithForgroundservice( LatLng(previouspoint!!.latitude, previouspoint!!.longitude) ,currentpoint)
                        Toast.makeText(context, distance.toString(), Toast.LENGTH_SHORT).show()
                        if (String.format("%.2f",  SphericalUtil.computeDistanceBetween(LatLng(previouspoint!!.latitude, previouspoint!!.longitude) ,currentpoint) / 1000).toDouble()  > 0.15.toString().toDouble()) {
                            Toast.makeText(context, "You are outside $distance", Toast.LENGTH_SHORT).show()

                            notifyText=  stringToLatLong("${currentpoint?.latitude.toString()},${currentpoint?.longitude.toString()}",context)+ ", Distance :"+String.format("%.2f", distance / 1000)  + "km"
                            previouspoint=currentpoint
                            var loc=Location("")
                            loc.latitude= previouspoint!!.latitude
                            loc.longitude= previouspoint!!.longitude


                            countTimer(context,loc)
                        }else{
                            previouspoint=currentpoint
                            var loc=Location("")
                            loc.latitude= previouspoint!!.latitude
                            loc.longitude= previouspoint!!.longitude


                            countTimer(context,loc)
                        }
                  //  }
                    startForeground(1, getNotification())
                }else{
                    // initialize snack bar
                    Toast.makeText(context, "Not Connected to Internet", Toast.LENGTH_SHORT).show()

                }

                //   textView.setText("done!")
            }
        }.start()
    }




}
