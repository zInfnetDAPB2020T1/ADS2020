package android.inflabnet.infsocial

import android.inflabnet.infsocial.viewmodel.UserViewModel
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var userViewModel = ViewModelProviders.of(this)[UserViewModel::class.java]
    }
}
