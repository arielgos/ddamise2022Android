package com.agos.ddamise2022.ui

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.agos.ddamise2022.Configuration
import com.agos.ddamise2022.R
import com.agos.ddamise2022.model.Location
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions


class ARoutes : AppCompatActivity() {

    private lateinit var myLocation: Location
    lateinit var myLatLong: LatLng
    private lateinit var configuration: Configuration
    private lateinit var mapFragment: SupportMapFragment
    var lastLatLong: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routes)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setTitle(R.string.routes)

        myLocation = intent?.extras?.getSerializable("myLocation") as Location
        myLatLong = LatLng(myLocation.latitude, myLocation.longitude)

        configuration = Configuration.create(this@ARoutes)

        mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment

        mapFragment.getMapAsync { map ->
            map.addCircle(
                CircleOptions()
                    .center(myLatLong)
                    .radius(20.0)
                    .strokeColor(Color.RED)
                    .fillColor(0x22FF0000)
                    .strokeWidth(3f)
            )
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLong, configuration.defaultZoom))

            map.setOnMapClickListener {
                map.addMarker(
                    MarkerOptions()
                        .position(it)
                )



                if (lastLatLong != null) {
                    map.addPolyline(
                        PolylineOptions()
                            .add(lastLatLong, it)
                            .color(Color.RED)
                    )
                } else {
                    map.addPolyline(
                        PolylineOptions()
                            .add(myLatLong, it)
                            .color(Color.RED)
                    )
                }

                lastLatLong = it

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_routes, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        mapFragment.getMapAsync {
            it.clear()
            it.addCircle(
                CircleOptions()
                    .center(myLatLong)
                    .radius(20.0)
                    .strokeColor(Color.RED)
                    .fillColor(0x22FF0000)
                    .strokeWidth(3f)
            )
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLong, configuration.defaultZoom))

            lastLatLong = null
        }

        return super.onOptionsItemSelected(item)
    }
}