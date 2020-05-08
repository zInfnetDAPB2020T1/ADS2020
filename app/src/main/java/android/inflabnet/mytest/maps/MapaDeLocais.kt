package android.inflabnet.mytest.maps

import android.content.Context
import android.inflabnet.infsocial.maps.PlacesRootClass
import android.inflabnet.mytest.R
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.GsonBuilder

class MapaDeLocais : AppCompatActivity(), OnMapReadyCallback {

    //mapa
    lateinit var context: Context
    lateinit var mMap: GoogleMap
    var nomeTipo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa_de_locais)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        nomeTipo = intent.getStringExtra("nomeTipo")
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        HitApi(this,-22.905922, -43.176588,10000,nomeTipo!!).execute()
    }
    private inner class HitApi: AsyncTask<Void, Void, String> {
        var context : Context? = null
        var lat : Double? = null
        var lng : Double? = null
        var radius : Int? = null
        var type : String? = null

        constructor(context: Context,lat: Double,lng: Double,radius: Int,type: String) {
            this.context = context
            this.lat = lat
            this.lng = lng
            this.radius = radius
            this.type = type
        }
        override fun doInBackground(vararg params: Void?): String {
            return GooglePlacesApi().getPlacesJson(context as Context,lat as  Double,lng as Double,radius as Int,type as String)

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            val gson = GsonBuilder().create()
            val root = gson.fromJson(result, PlacesRootClass::class.java)
            addMarkers(root)
        }
    }
    public fun addMarkers(root: PlacesRootClass){
        for (result  in root.results){
            val p  = LatLng(result.geometry.location.lat, result.geometry.location.lng)
            mMap.addMarker(MarkerOptions().position(p).title(result.name))
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(-22.905922, -43.176588)))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14f))
    }
}
