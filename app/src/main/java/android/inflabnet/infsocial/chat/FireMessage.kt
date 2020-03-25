package android.inflabnet.infsocial.chat

import android.inflabnet.infsocial.R
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_fire_message.*

class FireMessage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fire_message)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        nav_view.setOnNavigationItemReselectedListener {
            when(it.itemId){
                R.id.navigation_people ->{
                    //todo show people fragment
                    true
                }
                R.id.navigation_my_account ->{
                    //todo show myaccount fragment
                    true
                }
                else -> false
            }

        }
    }
}
