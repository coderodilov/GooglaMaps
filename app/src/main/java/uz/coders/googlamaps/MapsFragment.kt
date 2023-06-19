@file:Suppress("DEPRECATION")

package uz.coders.googlamaps

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices


import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class MapsFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest

    private lateinit var myLocation:LatLng
    private lateinit var googleMap:GoogleMap
    private var lastLocation:LatLng? = null
    private var marker: Marker? = null
    private var markersList = ArrayList<Marker>()

    private val callback = OnMapReadyCallback { googleMap ->
        this.googleMap = googleMap
        googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000
            fastestInterval = 1000
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                myLocation = LatLng(p0.lastLocation!!.latitude, p0.lastLocation!!.longitude)
                val icon = BitmapDescriptorFactory.fromResource(R.drawable.marker)
                val markerOptions = MarkerOptions().position(myLocation).icon(icon)

                marker = googleMap.addMarker(markerOptions)
                removeOldMarkers()
                markersList.add(marker!!)

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17f))

                if (lastLocation == null){
                    lastLocation = myLocation
                }

                googleMap.addPolyline(PolylineOptions().add(lastLocation).add(myLocation).color(Color.GREEN))
                lastLocation = myLocation
            }
        }

    }

    private fun removeOldMarkers() {
        for (marker in markersList) {
            marker.remove()
        }
        markersList.clear()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

}