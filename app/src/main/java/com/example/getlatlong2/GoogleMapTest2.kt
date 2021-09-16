package com.example.getlatlong

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.example.getlatlong2.databinding.ActivityGoogleMapTest2Binding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*


class GoogleMapTest : AppCompatActivity() {
    private lateinit var binding: ActivityGoogleMapTest2Binding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var position = LatLng(4.2105, 101.9758)

//    private var justStarted = true

    var markerOptions = MarkerOptions().position(position)

    lateinit var marker : Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoogleMapTest2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        with(binding.mapView) {
            // Initialise the MapView
            onCreate(null)
            // Set the map ready callback to receive the GoogleMap object
            getMapAsync{
                MapsInitializer.initialize(applicationContext)
                setMapLocation(it)
            }
        }

        binding.mapView.onCreate(null)
        binding.mapView.getMapAsync{
            MapsInitializer.initialize(applicationContext)
            setMapLocation(it)
        }

        onPause()

        binding.btnGetLocation2.setOnClickListener {
            checkPermissions()
        }
    }

    private fun setMapLocation(map : GoogleMap) {
        with(map) {
            val malaysia = position
            mapType = GoogleMap.MAP_TYPE_NORMAL
            map.addMarker(MarkerOptions().position(malaysia).title("You are Here"))
            moveCamera(CameraUpdateFactory.newLatLngZoom(malaysia, 13f))
//            map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 7f))

//            if(justStarted){
//                val malaysia = position
//                map.addMarker(MarkerOptions().position(malaysia).title("You are Here"))
//                map.moveCamera(CameraUpdateFactory.newLatLngZoom(malaysia, 7f))
//            }else{
//                val malaysia = position
//                map.addMarker(MarkerOptions().position(malaysia).title("You are Here"))
//                map.moveCamera(CameraUpdateFactory.newLatLngZoom(malaysia,16.0f))
//            }

//
            setOnMapClickListener {
                if(::marker.isInitialized){
                    marker.remove()
                }
                markerOptions.position(it)
                marker = addMarker(markerOptions)
                Toast.makeText(this@GoogleMapTest, "Clicked on" , Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }


    // =================================================================================================
    private fun checkPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        }else {
            getLocations()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocations() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            if (it == null){
                Toast.makeText(this, "Sorry Can't get Location", Toast.LENGTH_SHORT).show()
            }else it.apply {
                val latitude = it.latitude
                val longitude = it.longitude

//                justStarted = false

                position = LatLng(latitude, longitude)

                binding.mapView.onCreate(null)
                binding.mapView.getMapAsync{
                    MapsInitializer.initialize(applicationContext)
                    setMapLocation(it)
                }
                onResume()

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    getLocations()
                }else{
                    Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}