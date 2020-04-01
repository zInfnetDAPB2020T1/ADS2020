package android.inflabnet.mytest.fragments

import android.content.Intent
import android.inflabnet.mytest.maps.MapsActivity
import android.inflabnet.mytest.R
import android.inflabnet.mytest.chat.activity.MainActivityChat
import android.inflabnet.mytest.login.MainActivity
import android.inflabnet.mytest.login.SplashActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_home_menu.*

/**
 * A simple [Fragment] subclass.
 */
class HomeMenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnMapsMenu.setOnClickListener {
            val intt = Intent(this.context!!.applicationContext, MapsActivity::class.java)
            startActivity(intt)
        }

        btnSalas.setOnClickListener {
            findNavController().navigate(R.id.action_homeMenuFragment_to_criarSalaFragment)
        }

        btnDividirConta.setOnClickListener {
            findNavController().navigate(R.id.action_homeMenuFragment_to_dividirContaFragment)
        }

        btnChat.setOnClickListener {
            val intt = Intent(this.context!!.applicationContext, MainActivityChat::class.java)
            startActivity(intt)
        }
    }
}
