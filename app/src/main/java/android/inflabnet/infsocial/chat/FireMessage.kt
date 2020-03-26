package android.inflabnet.infsocial.chat

import android.app.Fragment
import android.inflabnet.infsocial.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.replace
import com.resocoder.firemessage.fragment.MyAccountFragment
import kotlinx.android.synthetic.main.activity_fire_message.*
import kotlinx.android.synthetic.main.activity_main.*

class FireMessage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fire_message)

        nav_view.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navigation_people ->{
                    //replaceFragment(PeopleFragment())
                    true
                }
                R.id.navigation_my_account ->{
                    val fragmentManager = supportFragmentManager
                    val transaction = fragmentManager.beginTransaction()

                    transaction.replace(R.id.fragment_layout,MyAccountFragment())
                    transaction.commit()

                    true
                }
                else -> false
            }

        }
    }

//    private fun replaceFragment(fragment: Fragment) {
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_layout, fragment)
//            .commit()
//    }
}


