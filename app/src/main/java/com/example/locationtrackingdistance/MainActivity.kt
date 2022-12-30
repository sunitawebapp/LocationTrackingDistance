package com.example.locationtrackingdistance

import android.Manifest
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.locationtrackingdistance.livedata.FASTEST_INTERVAL
import com.example.locationtrackingdistance.livedata.INTERVAL
import com.example.locationtrackingdistance.livedata.LocationViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil


class MainActivity : AppCompatActivity() /*, OnMapReadyCallback */{
  lateinit  var latitude : TextView
    lateinit  var longitude : TextView
    lateinit  var diatance : TextView
     var LOCATION_PERMISSION_CODE =1
     var BACKGROUND_LOCATION_PERMISSION_CODE =2

    var distance=0.0
    var startpoint : LatLng?=null
    var endpoint :LatLng?=null

    lateinit var locationRequest: LocationRequest
   /* lateinit var mMap: GoogleMap
    private  var marker: Marker?=null*/
   lateinit var fusedLocationProviderClient :FusedLocationProviderClient
    private val locationViewModel: LocationViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        diatance=findViewById(R.id.diatance)
        latitude=findViewById(R.id.latitude)
        longitude=findViewById(R.id.longitude)
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, LocationUpdatesService::class.java))
        }*/

/*
        var smf =  getChildFragmentManager().findFragmentById(R.id.map) as SupportMapFragment
        smf.getMapAsync(this);*/
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Fine Location permission is granted
            // Check if current android version >= 11, if >= 11 check for Background Location permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // Background Location Permission is granted so do your work here
                } else {
                    // Ask for Background Location Permission
                    askPermissionForBackgroundUsage();
                }
            }
        } else {
            // Fine Location Permission is not granted so ask for permission
            askForLocationPermission();
        }



         locationRequest = LocationRequest.create()
            .apply {
                interval = INTERVAL
                fastestInterval = FASTEST_INTERVAL
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
      /*  if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {


            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            getpendingIntent()
        )
*/
        locationViewModel.getLocationData.observe(this, Observer {
            longitude.text = it.longitude.toString()
            latitude.text = it.latitude.toString()
            //    info.text = getString(R.string.location_successfully_received)


            startpoint= LatLng(it.latitude, it.longitude)

            var distanceresult= distanceCalculate()
            diatance.text=String.format("%.2f", distanceresult / 1000) + "km"


        })
    }


    fun distanceCalculate ()  : Double{
        if (endpoint!=null){
            var thirdpont=startpoint
            distance = SphericalUtil.computeDistanceBetween(endpoint, thirdpont)+distance;
            Log.d("sunita", "observeLocationUpdates: "+endpoint+" "+thirdpont)
            Log.d("sunitya", "observeLocationUpdates: "+String.format("%.2f", distance / 1000) + "km")
            endpoint=thirdpont

        }else{
            distance = SphericalUtil.computeDistanceBetween(startpoint, startpoint)+distance;
            Log.d("sunita", "observeLocationUpdates: "+startpoint+" "+startpoint)
            endpoint=startpoint

            Log.d("sunitya", "observeLocationUpdates: "+String.format("%.2f", distance / 1000) + "km")
        }
        return  distance
    }

    private fun askForLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            AlertDialog.Builder(this)
                .setTitle("Permission Needed!")
                .setMessage("Location Permission Needed!")
                .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                        ActivityCompat.requestPermissions(
                            this@MainActivity, arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ), LOCATION_PERMISSION_CODE
                        )
                    })
                .setNegativeButton("CANCEL", DialogInterface.OnClickListener { dialog, which ->
                    // Permission is denied by the user
                })
                .create().show()


        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE
            )


        }
    }

    private fun askPermissionForBackgroundUsage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@MainActivity,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        ) {
            AlertDialog.Builder(this)
                .setTitle("Permission Needed!")
                .setMessage("Background Location Permission Needed!, tap \"Allow all time in the next screen\"")
                .setPositiveButton(
                    "OK"
                ) { dialog, which ->
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        BACKGROUND_LOCATION_PERMISSION_CODE
                    )
                }
                .setNegativeButton(
                    "CANCEL"
                ) { dialog, which ->
                    // User declined for Background Location Permission.
                }
                .create().show()


            locationViewModel.getLocationData.observe(this, Observer {
                longitude.text = it.longitude.toString()
                latitude.text = it.latitude.toString()
                //    info.text = getString(R.string.location_successfully_received)

            })


        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                BACKGROUND_LOCATION_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // User granted location permission
                // Now check if android version >= 11, if >= 11 check for Background Location Permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        // Background Location Permission is granted so do your work here
                    } else {
                        // Ask for Background Location Permission
                        askPermissionForBackgroundUsage();
                    }
                }
            } else {
                // User denied location permission
            }
        } else if (requestCode == BACKGROUND_LOCATION_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // User granted for Background Location Permission.
            } else {
                // User declined for Background Location Permission.
            }
        }

    }


   /* override fun onMapReady(map: GoogleMap) {
        mMap = map;

    }*/
    override fun onDestroy() {


        super.onDestroy()
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show()


   }

    fun getpendingIntent() : PendingIntent{
     /*   var  intent1 =  Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent1);*/
        var intent =Intent(this, LocationReceiver::class.java)
        intent.setAction("com.example.locationtrackingdistance.UPDATE_LOCATION")
        return PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT)
    }

   /* fun getCurrentloaction(){
        var  title=stringToLatLong("$lat,$lng")
        if(marker == null){
            marker = mMap!!.addMarker(
                MarkerOptions()
                    .position(LatLng(lat, lng))
                    .title(returnedAddress)

            )
        } else {
            marker?.position = LatLng(lat, lng)
            marker?.title = returnedAddress
        }

        val cameraPosition = CameraPosition.Builder().target(LatLng(lat, lng))
            .zoom(17f)
            .bearing(0f)
            .tilt(45f)
            .build()
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat, lng)))
        mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(12f), 2000, null);

    }*/
}
