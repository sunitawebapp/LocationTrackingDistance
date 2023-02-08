package com.sunitawebapp.locationtrackingdistance

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.sunitawebapp.locationtrackingdistance.AppController.Companion.applunchnew
import com.sunitawebapp.locationtrackingdistance.AppController.Companion.distancestore
import com.sunitawebapp.locationtrackingdistance.livedata.FASTEST_INTERVAL
import com.sunitawebapp.locationtrackingdistance.livedata.INTERVAL
import com.sunitawebapp.locationtrackingdistance.livedata.LocationViewModel
import com.sunitawebapp.locationtrackingdistance.service.LocationUpdatesService
import java.util.*


class MainActivity : AppCompatActivity() , OnMapReadyCallback, ConnectionReceiver.ReceiverListener {
    var  prepoint: LatLng?=null
  lateinit  var latitude : TextView
  lateinit  var longitude : TextView
  lateinit  var diatance : TextView
  lateinit var diatancetype2: TextView
  lateinit var diatancetype3 : TextView
  lateinit var diatanceWithContinueRun: TextView



  var LOCATION_UPDATE_INTERVAL=5000
    var LOCATION_UPDATE_FASTEST_INTERVAL=3000
  var LOCATION_PERMISSION_CODE =1
    var BACKGROUND_LOCATION_PERMISSION_CODE =2
    var currentpoint : LatLng?=null
    lateinit var locationRequest: LocationRequest
    lateinit var mMap: GoogleMap
    private  var marker: Marker?=null
    var returnedAddress=""
   lateinit var fusedLocationProviderClient :FusedLocationProviderClient
    private val locationViewModel: LocationViewModel by viewModels()
    lateinit var btnStart : Button
    lateinit var btnStop : Button
    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var delay = 1000
    var REQUEST_CODE_CHECK_SETTINGS=101
    var isarea=false

    var distance=0.0
    var previouspoint: LatLng? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        diatanceWithContinueRun=findViewById(R.id.diatanceWithContinueRun)
        diatance=findViewById(R.id.diatance)
        latitude=findViewById(R.id.latitude)
        longitude=findViewById(R.id.longitude)
        btnStart=findViewById(R.id.btnStart)
        btnStop=findViewById(R.id.btnStop)
        diatancetype2=findViewById(R.id.diatancetype2)
        diatancetype3=findViewById(R.id.diatancetype3)

   /*     Before 5m prelc of redious same as currlc

        if cuurle radius within same as prelc
*/

                checkGPS()

        FirebaseApp.initializeApp(this);



        var smf =  this@MainActivity.getSupportFragmentManager().findFragmentById(R.id.map) as SupportMapFragment
        smf.getMapAsync(this);
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
        if (applunchnew){
            var  prepoint: LatLng?=null
            locationViewModel.getLocationData.observe(this, Observer {
                longitude.text = it.longitude.toString()
                latitude.text = it.latitude.toString()
                //    info.text = getString(R.string.location_successfully_received)


                currentpoint= LatLng(it.latitude, it.longitude)

                diatance.text=String.format("%.2f", AppController.distanceCalculate(currentpoint) / 1000) + "km"
                // time count down for 30 seconds,
                // with 1 second as countDown interval


                // storedata(it,distanceresult)

                try {
                    if (checkConnection()){



                        getCurrentloaction(it)
                        getDistanceNegligibleMeter(it)
                        //getCurrentloactionithContinueRun(it,prepoint)
                    }else{
                        // initialize snack bar
                        //Toast.makeText(this@MainActivity, "Not Connected to Internet", Toast.LENGTH_SHORT).show()
                        Snackbar.make(findViewById(R.id.layout), "Not Connected to Internet", Snackbar.LENGTH_LONG).show()
                    }

                }catch (e : Exception){

                }

                /* var firebasedatabase= FirebaseDatabase.getInstance("https://locationonmap-483ef-default-rtdb.firebaseio.com/")

                  var databasereference= firebasedatabase.getReference("GiriExp").child("Location").child("3")
                  databasereference.child("CurLatLng").child("Lat").setValue(it.latitude.toString())
                  databasereference.child("CurLatLng").child("Lng").setValue(it.longitude.toString())
                  databasereference.child("Distance").setValue(String.format("%.2f", distance!! / 1000) + "km")  */

            })
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(Intent(this, LocationUpdatesService::class.java))
            }
        }
        btnStart.setOnClickListener {
            applunchnew=true


            locationViewModel.getLocationData.observe(this, Observer {

                longitude.text = it.longitude.toString()
                latitude.text = it.latitude.toString()
                //    info.text = getString(R.string.location_successfully_received)


                currentpoint= LatLng(it.latitude, it.longitude)



                diatance.text=String.format("%.2f", AppController.distanceCalculate(currentpoint) / 1000) + "km"



                // storedata(it,distanceresult)

                try {
                    if (checkConnection()){

                            getCurrentloaction(it)
                        getDistanceNegligibleMeter(it)
                       /* if (previouspoint==null){
                            previouspoint=LatLng(it.latitude, it.longitude)
                            countTimer()

                        }*/
                    }else{
                        // initialize snack bar
                      //  Toast.makeText(this@MainActivity, "Not Connected to Internet", Toast.LENGTH_SHORT).show()
                       Snackbar.make(findViewById(R.id.layout), "Not Connected to Internet", Snackbar.LENGTH_LONG).show()

                    }

                }catch (e : Exception){

                }


                /* var firebasedatabase= FirebaseDatabase.getInstance("https://locationonmap-483ef-default-rtdb.firebaseio.com/")

                  var databasereference= firebasedatabase.getReference("GiriExp").child("Location").child("3")
                  databasereference.child("CurLatLng").child("Lat").setValue(it.latitude.toString())
                  databasereference.child("CurLatLng").child("Lng").setValue(it.longitude.toString())
                  databasereference.child("Distance").setValue(String.format("%.2f", distance!! / 1000) + "km")  */

            })
            /*currentpoint?.let {
                countTimer()
            }
*/


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(Intent(this, LocationUpdatesService::class.java))
            }
        }
        btnStop.setOnClickListener {

            stopService(Intent(this, LocationUpdatesService::class.java))

        }

    }


   fun checkGPS(){
    val locationRequest = LocationRequest.create()
        .setInterval(LOCATION_UPDATE_INTERVAL.toLong())
        .setFastestInterval(LOCATION_UPDATE_FASTEST_INTERVAL.toLong())
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

    val builder = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)

    LocationServices
        .getSettingsClient(this)
        .checkLocationSettings(builder.build())
        .addOnSuccessListener(
            this
        ) { response: LocationSettingsResponse? -> }
        .addOnFailureListener(
            this
        ) { ex: java.lang.Exception? ->
            if (ex is ResolvableApiException) {
                // Location settings are NOT satisfied,  but this can be fixed  by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),  and check the result in onActivityResult().
                    val resolvable = ex as ResolvableApiException
                    resolvable.startResolutionForResult(
                        this@MainActivity,
                        REQUEST_CODE_CHECK_SETTINGS
                    )
                } catch (sendEx: SendIntentException) {
                    // Ignore the error.
                }
            }
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (REQUEST_CODE_CHECK_SETTINGS == requestCode) {
            if(Activity.RESULT_OK == resultCode){
                //user clicked OK, you can startUpdatingLocation(...);

            }else{
                //user clicked cancel: informUserImportanceOfLocationAndPresentRequestAgain();
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onMapReady(map: GoogleMap) {
        mMap = map;

    }
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

    fun getCurrentloaction(it : Location) {

        var  title=stringToLatLong("${it.latitude.toString()},${it.longitude.toString()}")
     //     var markertrack: Marker?=null
        if(marker == null){
            marker = mMap!!.addMarker(MarkerOptions().position(LatLng(it.latitude, it.longitude))
                .title(returnedAddress)
                .icon(BitmapFromVector(getApplicationContext(), R.drawable.directions_bike_icon))

                )
            marker?.showInfoWindow()

        } else {
            marker?.position = LatLng(it.latitude, it.longitude)
            marker?.title=returnedAddress
            marker?.showInfoWindow()

          //  marker?.title = returnedAddress
        }

        mMap.isMyLocationEnabled = true
        val cameraPosition = CameraPosition.Builder().target(LatLng(it.latitude, it.longitude))
            .zoom(17f)
            .bearing(0f)
            .tilt(45f)
            .build()
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 15f))
        mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null);

    }
fun getDistanceNegligibleMeter(it : Location){
    if (previouspoint==null){
        var areaCircle=  mMap.addCircle(CircleOptions().radius(150.0)
            .center(LatLng(it.latitude, it.longitude))
            .fillColor(Color.TRANSPARENT)  //default
            .strokeColor(Color.TRANSPARENT)

        )



        val distance = FloatArray(2)

        Location.distanceBetween(
            marker!!.getPosition().latitude, marker!!.getPosition().longitude,
            areaCircle.getCenter().latitude, areaCircle.getCenter().longitude, distance
        )

        if (distance[0] > areaCircle.getRadius()) {
            isarea=false
            diatancetype3.text=String.format("%.2f", AppController.distanceCalculateNegligibleMeter(LatLng(it!!.latitude,it!!.longitude)) / 1000) + "km"
            diatancetype2.text=String.format("%.2f", AppController.distanceWithContinueRunCalculate( LatLng(previouspoint!!.latitude, previouspoint!!.longitude) ,LatLng(it!!.latitude,it!!.longitude)) / 1000) + "km"
          //  var distancereslt=AppController.distanceCalculateNegligibleMeter(LatLng(it.latitude, it.longitude))
        //    diatancetype2.text=String.format("%.2f", distancereslt / 1000) + "km"
            Toast.makeText(this@MainActivity, "outside", Toast.LENGTH_LONG).show()
            previouspoint=LatLng(it!!.latitude,it!!.longitude)

        } else {
            isarea=true
            Toast.makeText(this@MainActivity, "Inside", Toast.LENGTH_LONG).show()
            previouspoint=LatLng(it!!.latitude,it!!.longitude)

        }
    }else{
        var areaCircle=  mMap.addCircle(CircleOptions().radius(150.0)
            .center(previouspoint?.let { it1 -> LatLng(previouspoint!!.latitude, previouspoint!!.longitude)})
            .fillColor(Color.TRANSPARENT)  //default
            .strokeColor(Color.TRANSPARENT)
        )



        val distance = FloatArray(2)

        Location.distanceBetween(
            marker!!.getPosition().latitude, marker!!.getPosition().longitude,
            areaCircle.getCenter().latitude, areaCircle.getCenter().longitude, distance

        )

        if (distance[0] > areaCircle.getRadius()) {
            isarea=false
            diatancetype2.text=String.format("%.2f", AppController.distanceWithContinueRunCalculate( LatLng(previouspoint!!.latitude, previouspoint!!.longitude) ,LatLng(it!!.latitude,it!!.longitude)) / 1000) + "km"
            diatancetype3.text=String.format("%.2f", AppController.distanceCalculateNegligibleMeter(LatLng(it!!.latitude,it!!.longitude)) / 1000) + "km"
            Toast.makeText(this@MainActivity, "outside", Toast.LENGTH_LONG).show()

               previouspoint=LatLng(it!!.latitude,it!!.longitude)
            /*    if (checkConnection()){

                    countTimer()
                }else{
                    // initialize snack bar
                    //  Toast.makeText(this@MainActivity, "Not Connected to Internet", Toast.LENGTH_SHORT).show()
                    Snackbar.make(findViewById(R.id.layout), "Not Connected to Internet", Snackbar.LENGTH_LONG).show()

                }*/



        } else {
            isarea=true
            Toast.makeText(this@MainActivity, "Inside", Toast.LENGTH_LONG).show()
            previouspoint=LatLng(it!!.latitude,it!!.longitude)
            /*   if (checkConnection()){

                   countTimer()
               }else{
                   // initialize snack bar
                   //  Toast.makeText(this@MainActivity, "Not Connected to Internet", Toast.LENGTH_SHORT).show()
                   Snackbar.make(findViewById(R.id.layout), "Not Connected to Internet", Snackbar.LENGTH_LONG).show()

               }*/


        }
    }

}

    private fun BitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        // below line is use to generate a drawable.
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)

        // below line is use to set bounds to our vector drawable.
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )

        // below line is use to create a bitmap for our
        // drawable which we have added.
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        // below line is use to add bitmap in our canvas.
        val canvas = Canvas(bitmap)

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas)

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
    fun stringToLatLong(latLongStr: String): LatLng {
        val geocoder = Geocoder(this, Locale.getDefault())


        val latlong = latLongStr.split(",").toTypedArray()
        val latitude = latlong[0].toDouble()
        val longitude = latlong[1].toDouble()

        val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)
        val strReturnedAddress = StringBuilder("")
        var  returnedAdd = addresses[0]

        for (i in 0..returnedAdd.getMaxAddressLineIndex()) {
            strReturnedAddress.append(returnedAdd.getAddressLine(i)).append("\n")
        }
        returnedAddress = strReturnedAddress.toString()
        return LatLng(latitude, longitude)
    }

     fun checkConnection() : Boolean{

        // initialize intent filter
        val intentFilter = IntentFilter()

        // add action
        intentFilter.addAction("android.new.conn.CONNECTIVITY_CHANGE")

        // register receiver
        registerReceiver(ConnectionReceiver(), intentFilter)

        // Initialize listener
        ConnectionReceiver.Listener = this

        // Initialize connectivity manager
        val manager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Initialize network info
        val networkInfo = manager.activeNetworkInfo

        // get connection status
        val isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting

        // display snack bar
         var isconnected= showSnackBar(isConnected)
         return isconnected
    }

    override fun onNetworkChange(isConnected: Boolean) {
        // display snack bar
        showSnackBar(isConnected);
    }


    private fun showSnackBar(isConnected: Boolean) :Boolean{

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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        // initialize snack bar
       // val snackbar = Snackbar.make(findViewById(R.id.btn_check), message, Snackbar.LENGTH_LONG)


        // initialize view
      //  val view: View = snackbar.view

      /*  // Assign variable
        val textView: TextView = view.findViewById(R.id.snackbar_text)

        // set text color
        textView.setTextColor(color)*/

        // show snack bar
    //    snackbar.show()
        return false
    }

    fun countTimer() {

       // object : CountDownTimer(180000, 1000) {
            object : CountDownTimer(300000, 1000) {

            // Callback function, fired on regular interval
            override fun onTick(millisUntilFinished: Long) {
                //    textView.setText("seconds remaining: " + millisUntilFinished / 1000)
                Log.d("timeing", "onTick: seconds remaining"+millisUntilFinished / 1000)


            }

            // Callback function, fired
            // when the time is up
            override fun onFinish() {

                Log.d("timeing", "onTick: seconds remaining done")

                var areaCircle=  mMap.addCircle(CircleOptions().radius(10.0)
                    .center(previouspoint?.let { it1 -> LatLng(previouspoint!!.latitude, previouspoint!!.longitude) })
                    .fillColor(Color.TRANSPARENT)  //default
                    .strokeColor(Color.TRANSPARENT)
                   /* .strokeWidth(5f)
                    .strokeColor(Color.RED)
                    .fillColor(0x550000FF)*/
                )


                val distance = FloatArray(2)

                Location.distanceBetween(
                    marker!!.getPosition().latitude, marker!!.getPosition().longitude,
                    areaCircle.getCenter().latitude, areaCircle.getCenter().longitude, distance
                )

                if (distance[0] > areaCircle.getRadius()) {
                    isarea=false
                    var distanceresult=AppController.distanceWithContinueRunCalculate( LatLng(previouspoint!!.latitude, previouspoint!!.longitude) ,LatLng(currentpoint!!.latitude,currentpoint!!.longitude))
               //   distancestore=   distancestore+distanceresult
                    diatanceWithContinueRun.text=String.format("%.2f", distanceresult / 1000) + "km"
                    Toast.makeText(this@MainActivity, "outside", Toast.LENGTH_LONG).show()

                    previouspoint=LatLng(currentpoint!!.latitude,currentpoint!!.longitude)
                    if (checkConnection()){

                        countTimer()
                    }else{
                        // initialize snack bar
                        //  Toast.makeText(this@MainActivity, "Not Connected to Internet", Toast.LENGTH_SHORT).show()
                        Snackbar.make(findViewById(R.id.layout), "Not Connected to Internet", Snackbar.LENGTH_LONG).show()

                    }



                } else {
                    isarea=true
                    Toast.makeText(this@MainActivity, "Inside", Toast.LENGTH_LONG).show()
                    previouspoint=LatLng(currentpoint!!.latitude,currentpoint!!.longitude)
                    if (checkConnection()){

                        countTimer()
                    }else{
                        // initialize snack bar
                        //  Toast.makeText(this@MainActivity, "Not Connected to Internet", Toast.LENGTH_SHORT).show()
                        Snackbar.make(findViewById(R.id.layout), "Not Connected to Internet", Snackbar.LENGTH_LONG).show()

                    }


                }
                //   textView.setText("done!")
            }
        }.start()

    }
}
