package com.sunitawebapp.locationtrackingdistance

import android.app.Application
import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.FirebaseDatabase
import com.google.maps.android.SphericalUtil


class AppController : Application() {

    override fun onCreate() {
        super.onCreate()

    }

    companion object{
        var  distancestore=0.0
        var applunchnew : Boolean=false
        var distance=0.0
        var previouspoint: LatLng? = null

        var distan=0.0
        var previpoint: LatLng? = null
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

        fun distanceWithContinueRunCalculate (previousPoint: LatLng?, currentPoint: LatLng?)  : Double {
            distan = SphericalUtil.computeDistanceBetween(previousPoint, currentPoint) + distan;
            return distan
        }

        fun storedata(it : Location,distanceresult : Double){
            var firebasedatabase= FirebaseDatabase.getInstance("https://locationonmap-483ef-default-rtdb.firebaseio.com/")

            var databasereference= firebasedatabase.getReference("GiriExp").child("Location").child("3")
            databasereference.child("CurLatLng").child("Lat").setValue(it.latitude.toString())
            databasereference.child("CurLatLng").child("Lng").setValue(it.longitude.toString())
            databasereference.child("Distance").setValue(String.format("%.2f", distanceresult!! / 1000) + "km")
        }
    }


}
