package android.inflabnet.mytest.fragments


import android.content.Intent
import android.inflabnet.mytest.R
import android.inflabnet.mytest.maps.HomeMapsActivity
import android.inflabnet.mytest.mesas.activity.MesaActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

        return inflater.inflate(R.layout.fragment_home_menu, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnMapsMenu.setOnClickListener {
            val intt = Intent(this.context!!.applicationContext, HomeMapsActivity::class.java)
            startActivity(intt)
        }

        btnMesas.setOnClickListener {
            val intt = Intent(this.context!!.applicationContext, MesaActivity::class.java)
            startActivity(intt)
        }

        btnTelaOrcamento.setOnClickListener {
            findNavController().navigate(R.id.action_homeMenuFragment_to_orcamentoFragment)

        }

    }
}
