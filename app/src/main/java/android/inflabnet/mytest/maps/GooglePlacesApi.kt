package android.inflabnet.mytest.maps

import android.content.Context
import android.inflabnet.mytest.R

import android.util.Log
import com.google.android.gms.common.internal.ImagesContract.URL
import java.net.HttpURLConnection
import java.net.URL

class GooglePlacesApi {
    public fun getPlacesJson (context : Context, lat : Double, lng : Double, radius : Int, type : String) : String{
        var result = ""
        try {
            var key: String = context.resources.getString(R.string.places_api_key)
            var urlStr: String = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$lat,$lng&radius=$radius&type=$type&key=$key"
            var url : URL = URL(urlStr)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection

            connection.setRequestProperty("Content-Type", "application/json")
            connection.requestMethod = "GET"
            connection.doInput = true
            Log.i("TARTAR", connection.toString())
            val br = connection.inputStream.bufferedReader()

            result = br.use {br.readText()}

            connection.disconnect()

        }catch (e: Exception){
            e.printStackTrace()
        }

        return result
    }
}