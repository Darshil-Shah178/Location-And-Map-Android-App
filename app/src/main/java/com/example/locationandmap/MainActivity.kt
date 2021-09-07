package com.example.locationandmap

import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.map
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

class MainActivity : AppCompatActivity() {
    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var marker: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(
            this,
            PreferenceManager.getDefaultSharedPreferences(this)
        )
        setContentView(R.layout.activity_main)
        var fastestSpeed = 0.0F
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        map.controller.setZoom(17.0)

        marker = Marker(map)
        marker.icon = AppCompatResources.getDrawable(
            this,
            R.drawable.ic_baseline_location_on_24
        )
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        locationClient = LocationServices.getFusedLocationProviderClient(this)

        fun getAddress(lat: Double, lng: Double): String {
            val geocoder = Geocoder(this)
            val list = geocoder.getFromLocation(lat, lng, 1)
            return list[0].getAddressLine(0)
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {

                locationResult ?: return
                for (location in locationResult.locations) {


                    speedTxt.text = getString(R.string.speed, (location.speed * 3.6))
                    if (fastestSpeed < location.speed) {
                        fastestSpeed = location.speed
                    }
                    fastestSpeedTxt.text = getString(R.string.speed, (fastestSpeed * 3.6))
                }
                val location = locationResult.lastLocation
                map.controller.setCenter(GeoPoint(location.latitude, location.longitude))
                marker.position = GeoPoint(location.latitude, location.longitude)
                marker.title = "${
                    getAddress(
                        location.latitude,
                        location.longitude
                    )
                },${location.latitude},${location.longitude},${location.altitude}"
                marker.closeInfoWindow()
                map.overlays.add(marker)
                map.invalidate()
            }
        }

        startTrackingBtn.setOnClickListener {
            Toast.makeText(applicationContext, "Tracking Current Location", Toast.LENGTH_SHORT)
                .show()
            val locationRequest = LocationRequest
                .create()
                .setInterval(1000)
                .setPriority(PRIORITY_HIGH_ACCURACY)

            if ((Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ) !=
                        PackageManager.PERMISSION_GRANTED)
            ) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    0
                )
            } else {

                locationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback, Looper.getMainLooper()
                )
            }
        }

        stopTrackingBtn.setOnClickListener {
            locationClient.removeLocationUpdates(locationCallback)
        }


    }
}