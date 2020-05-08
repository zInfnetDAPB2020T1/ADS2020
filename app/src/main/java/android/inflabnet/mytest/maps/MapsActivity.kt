package android.inflabnet.mytest.maps

import android.content.Context
import android.content.pm.PackageManager
import android.inflabnet.infsocial.maps.PlacesRootClass
import android.inflabnet.mytest.R
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.webkit.PermissionRequest
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.Status

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.gson.GsonBuilder
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_maps.*
import java.util.*

class MapsActivity : AppCompatActivity() {

    //places
    var placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS)
    lateinit var placesClient: PlacesClient
    internal var placeId=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        //places
        requestPermission()
        initPlaces()
        setupPlacesAutocomplete()
        setupCurrentPlace()
        setupGetPhotoandDetail()
    }




    //places abaixo
    private fun requestPermission() {
        Dexter.withActivity(this)
            .withPermissions(Arrays.asList(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION

            ))
            .withListener(object:MultiplePermissionsListener{
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                }
                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    Toast.makeText(this@MapsActivity,"A permissão é necessária", Toast.LENGTH_LONG).show()
                }
            }).check()

    }

    private fun initPlaces() {
        Places.initialize(this, getString(R.string.google_maps_key))
        placesClient = Places.createClient(this)
    }

    private fun setupPlacesAutocomplete() {
        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.fragment_place) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(placeFields)

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place) {
                Toast.makeText(this@MapsActivity, "" + p0.address, Toast.LENGTH_LONG).show()
            }

            override fun onError(p0: Status) {
                Toast.makeText(this@MapsActivity, "" + p0.statusMessage, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setupCurrentPlace() {
        val request = FindCurrentPlaceRequest.builder(placeFields).build()

        btn_get_current_place.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this@MapsActivity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@setOnClickListener;
            }

            val placeResponse = placesClient.findCurrentPlace(request)

            placeResponse.addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    val response = task.result
                    Log.i("MAPSERRO", response.toString())

//                    response!!.placeLikelihoods.sortWith()
//                    { placeChildhood, t1 ->
//                        placeChildhood.likelihood.toDouble().compareTo(t1.likelihood.toDouble())
//
//                    })
//                    Collections.reverse(response.placeLikelihoods)
                    placeId = response!!.placeLikelihoods[0].place.id!!
                    val likehoods = StringBuilder("")
                    edt_address.setText(StringBuilder(response.placeLikelihoods[0].place.address!!))

                    for (placeLikelihood in response.placeLikelihoods)
                    {
                        likehoods.append(
                            String.format(
                                "Place '%s' has likelihood: %f",
                                placeLikelihood.place.name,
                                placeLikelihood.likelihood))
                            .append("\n")
                    }
                    edt_place_likelihoods.setText(likehoods.toString())
                }
                else
                {
                    Toast.makeText(this, "Local não encontrado", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    private fun sortByLikelihood(placeLikelihoods: MutableList<PlaceLikelihood>) {

        placeLikelihoods.sortByDescending { it.likelihood }

    }
    private fun setupGetPhotoandDetail() {
        btn_get_photo.setOnClickListener {
            if (TextUtils.isEmpty(placeId)) {
                Toast.makeText(this@MapsActivity, "Place ID não pode ser nulo", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            getPhoto(placeId)
        }
    }
    private fun getPhoto(placeId: String) {
        val placeRequest = FetchPlaceRequest.builder(
            placeId,
            Arrays.asList(Place.Field.PHOTO_METADATAS,
                Place.Field.LAT_LNG)).build()

        placesClient.fetchPlace(placeRequest).addOnSuccessListener { fetchPlaceResponse ->
            val place = fetchPlaceResponse.place

            //get Lang Lat
            txt_detail.text = StringBuilder(place.latLng!!.latitude.toString())
                .append("/")
                .append(place.latLng!!.longitude.toString())
            //Get photo
            try {
                val photoMetaData = place.photoMetadatas!![0]
                //Criar a requesição
                val photoRequest = FetchPhotoRequest.builder(photoMetaData).build()
                placesClient.fetchPhoto(photoRequest).addOnSuccessListener { fetchPhotoResponse ->
                    val bitmap = fetchPhotoResponse.bitmap
                    image_view.setImageBitmap(bitmap)
                }
            } catch (e: Exception){
                Toast.makeText(this,"Foto não disponível para esse local",Toast.LENGTH_SHORT).show()
            }
        }
    }

}