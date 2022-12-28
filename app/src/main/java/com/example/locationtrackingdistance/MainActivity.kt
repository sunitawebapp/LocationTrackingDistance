package com.example.locationtrackingdistance

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.locationtrackingdistance.livedata.FASTEST_INTERVAL
import com.example.locationtrackingdistance.livedata.INTERVAL
import com.example.locationtrackingdistance.livedata.LocationLiveData
import com.example.locationtrackingdistance.livedata.LocationViewModel
import com.example.locationtrackingdistance.service.LocationUpdatesService
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity() /*, OnMapReadyCallback */{
  lateinit  var latitude : TextView
    lateinit  var longitude : TextView
    lateinit  var diatance : TextView
   /* lateinit var mMap: GoogleMap
    private  var marker: Marker?=null*/
   private var fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
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

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1);
            }else{
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1);
            }
        }


        val locationRequest: LocationRequest = LocationRequest.create()
            .apply {
                interval = INTERVAL
                fastestInterval = FASTEST_INTERVAL
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
        fusedLocationProviderClient.requestLocationUpdates(
           locationRequest,
            getpendingIntent()
        )


     /*   locationViewModel.getLocationData.observe(this, Observer {
            longitude.text = it.longitude.toString()
            latitude.text = it.latitude.toString()
            //    info.text = getString(R.string.location_successfully_received)

        })*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        locationViewModel.getLocationData.observe(this, Observer {
            longitude.text = it.longitude.toString()
            latitude.text = it.latitude.toString()
            //    info.text = getString(R.string.location_successfully_received)

        })
    }
   /* override fun onMapReady(map: GoogleMap) {
        mMap = map;

    }*/
    override fun onDestroy() {


        super.onDestroy()
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show()


   }

    fun getpendingIntent() : PendingIntent{
        var intent =Intent(this, LocationUpdatesService::class.java)
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
