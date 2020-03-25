package android.inflabnet.infsocial.chat

import android.inflabnet.infsocial.activities.MainActivity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.startActivity

class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //FirebaseApp.initializeApp(this)

        if (FirebaseAuth.getInstance().currentUser == null)
            startActivity<SignActivity>()
        else
            startActivity<FireMessage>()
        finish()
    }
}
