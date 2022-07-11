package com.agos.ddamise2022.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.agos.ddamise2022.Configuration
import com.agos.ddamise2022.R
import com.agos.ddamise2022.model.Location
import com.agos.ddamise2022.service.Foreground
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class AMain : AppCompatActivity() {

    private lateinit var configuration: Configuration

    private var serviceIntent: Intent? = null
    private var myLocation: Location? = null

    lateinit var mapFragment: SupportMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        configuration = Configuration.create(this@AMain)

        mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment

        mapFragment.getMapAsync {
            it.addMarker(
                MarkerOptions()
                    .position(LatLng(0.0, 0.0))
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        var intent: Intent? = null

        when (item.itemId) {
            R.id.action_search -> {
                Log.d(Configuration.tag, "Search")
                intent = Intent(this@AMain, ASearch::class.java)

            }
            R.id.action_firebase -> {
                Log.d(Configuration.tag, "Firebase")
                intent = Intent(this@AMain, AFirebase::class.java)
            }
            R.id.action_route -> {
                Log.d(Configuration.tag, "Route")
                intent = Intent(this@AMain, ARoutes::class.java)
            }
        }

        intent?.putExtra("myLocation", myLocation)
        startActivity(intent)

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(applicationContext)
            .registerReceiver(messageReceiver, IntentFilter(Configuration.tag))
        if (serviceIntent == null) {
            serviceIntent = Intent(applicationContext, Foreground::class.java)
        }
        ContextCompat.startForegroundService(applicationContext, serviceIntent!!)
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(applicationContext)
            .unregisterReceiver(messageReceiver)
        if (serviceIntent != null) {
            stopService(serviceIntent)
        }
    }

    private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val extras = intent.extras
            val location = Location(
                latitude = extras?.get("latitude").toString().toDouble(),
                longitude = extras?.get("longitude").toString().toDouble(),
                accuracy = extras?.get("accuracy").toString().toDouble()
            )

            Log.d(Configuration.tag, "Location [${location.latitude};${location.longitude} / ${location.accuracy}]}")

            if (myLocation == null) {
                var myLatLng = LatLng(location.latitude, location.longitude)
                //center map
                mapFragment.getMapAsync {
                    it.clear()
                    it.addMarker(
                        MarkerOptions()
                            .position(myLatLng)
                            .title(getString(R.string.my_location))
                    )
                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, configuration.defaultZoom))
                }
            }
            myLocation = location
        }
    }
}