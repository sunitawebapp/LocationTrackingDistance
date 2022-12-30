package com.example.locationtrackingdistance

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.location.LocationResult

class LocationReceiver : BroadcastReceiver() {
    val ACTION_PROCESS_UPDATE="com.example.locationtrackingdistance.UPDATE_LOCATION"
    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent!=null){
            var action =intent.action
            if (ACTION_PROCESS_UPDATE.equals(action)){
                var result =LocationResult.extractResult(intent)
                if (result!=null){
                    var location = result.lastLocation
                    Toast.makeText(context,""+location?.latitude , Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}