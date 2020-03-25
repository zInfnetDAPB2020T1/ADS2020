package android.inflabnet.infsocial.activities

import android.inflabnet.infsocial.R
import android.inflabnet.infsocial.viewmodel.UserViewModel
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var userViewModel = ViewModelProviders.of(this)[UserViewModel::class.java]

        FirebaseApp.initializeApp(this)
    }
}
