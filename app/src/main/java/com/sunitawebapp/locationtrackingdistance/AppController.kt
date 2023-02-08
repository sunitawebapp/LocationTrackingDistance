package com.sunitawebapp.locationtrackingdistance

import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.graphics.Color
import android.location.Location
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.FirebaseDatabase
import com.google.maps.android.SphericalUtil


class AppController : Application(){

    override fun onCreate() {
        super.onCreate()

    }

    companion object : ConnectionReceiver.ReceiverListener {
        var  distancestore=0.0
        var applunchnew : Boolean=false
        var distance=0.0
        var previouspoint: LatLng? = null

        var distan=0.0
        var forgroundServicedistan=0.0
        var previpoint: LatLng? = null
        var prevpoint: LatLng? = null
        var caldistance=0.0
        fun distanceCalculate ( currentPoint: LatLng?)  : Double {
            if (previouspoint != null) {
                var currtpoint = currentPoint
                distance = SphericalUtil.computeDistanceBetween(previouspoint, currtpoint) + distance;
                Log.d("sunita", "observeLocationUpdates: " + previouspoint + " " + currtpoint)
                Log.d(
                    "sunitya",
                    "observeLocationUpdates: " + String.format("%.2f", distance / 1000) + "km"
                )
                previouspoint = currtpoint

            }
            else {
                distance = SphericalUtil.computeDistanceBetween(currentPoint, currentPoint) + distance;
                Log.d("sunita", "observeLocationUpdates: " + currentPoint + " " + currentPoint)
                previouspoint = currentPoint

                Log.d(
                    "sunitya",
                    "observeLocationUpdates: " + String.format("%.2f", distance / 1000) + "km"
                )
            }
            return distance
        }

        fun distanceCalculateNegligibleMeter ( currentPoint: LatLng?)  : Double {
            if (prevpoint != null) {
                var currtpoint = currentPoint
                caldistance = SphericalUtil.computeDistanceBetween(prevpoint, currtpoint) + caldistance;
                Log.d("sunita", "observeLocationUpdates: " + prevpoint + " " + currtpoint)
                Log.d(
                    "sunitya",
                    "observeLocationUpdates: " + String.format("%.2f", caldistance / 1000) + "km"
                )
                prevpoint = currtpoint

            }
            else {
                caldistance = SphericalUtil.computeDistanceBetween(currentPoint, currentPoint) + caldistance;
                Log.d("sunita", "observeLocationUpdates: " + currentPoint + " " + currentPoint)
                prevpoint = currentPoint

                Log.d(
                    "sunitya",
                    "observeLocationUpdates: " + String.format("%.2f", caldistance / 1000) + "km"
                )
            }
            return caldistance
        }

        fun distanceWithContinueRunCalculate (previousPoint: LatLng?, currentPoint: LatLng?)  : Double {
            distan = SphericalUtil.computeDistanceBetween(previousPoint, currentPoint) + distan;
            return distan
        }

        fun distanceCalculatewithForgroundservice (previousPoint: LatLng?, currentPoint: LatLng?)  : Double {
            forgroundServicedistan = SphericalUtil.computeDistanceBetween(previousPoint, currentPoint) + forgroundServicedistan;
            return forgroundServicedistan
          /*  val selected_location = Location("locationA")
            selected_location.latitude = previousPoint!!. latitude
            selected_location.longitude = previousPoint!!. longitude
            val near_locations = Location("locationB")
            near_locations.latitude = currentPoint!!.latitude
            near_locations.longitude = currentPoint!!.longitude

            forgroundServicedistan =  selected_location.distanceTo(near_locations)+ forgroundServicedistan;
            return forgroundServicedistan*/
        }

        fun storedata(it : Location,distanceresult : Double){
            var firebasedatabase= FirebaseDatabase.getInstance("https://locationonmap-483ef-default-rtdb.firebaseio.com/")

            var databasereference= firebasedatabase.getReference("GiriExp").child("Location").child("3")
            databasereference.child("CurLatLng").child("Lat").setValue(it.latitude.toString())
            databasereference.child("CurLatLng").child("Lng").setValue(it.longitude.toString())
            databasereference.child("Distance").setValue(String.format("%.2f", distanceresult!! / 1000) + "km")
        }



        fun checkConnection(context: Context) : Boolean{

            // initialize intent filter
            val intentFilter = IntentFilter()

            // add action
            intentFilter.addAction("android.new.conn.CONNECTIVITY_CHANGE")

            // register receiver
          context.registerReceiver(ConnectionReceiver(), intentFilter)

            // Initialize listener
            ConnectionReceiver.Listener = this

            // Initialize connectivity manager
            val manager =
                context. getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            // Initialize network info
            val networkInfo = manager.activeNetworkInfo

            // get connection status
            val isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting

            // display snack bar
            var isconnected= showSnackBar(isConnected,context)
            return isconnected
        }

        override fun onNetworkChange(isConnected: Boolean) {
           // showSnackBar(isConnected);
        }

        fun showSnackBar(isConnected: Boolean,context: Context) :Boolean{

            // initialize color and message
            val message: String
            val color: Int

            // check condition
            if (isConnected) {

                // when internet is connected
                // set message
                message = "Connected to Internet"

                // set text color
                color = Color.WHITE
                return true
            } else {

                // when internet is disconnected
                // set message
                message = "Not Connected to Internet"

                // set text color
                color = Color.RED
                return false
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

            return false
        }

    }




}
