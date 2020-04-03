package android.inflabnet.mytest.fragments

import android.content.Intent
import android.inflabnet.mytest.maps.MapsActivity
import android.inflabnet.mytest.R
import android.inflabnet.mytest.database.OrcDBHelper
import android.inflabnet.mytest.mesas.activity.MesaActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders


import kotlinx.android.synthetic.main.fragment_home_menu.*

/**
 * A simple [Fragment] subclass.
 */
class HomeMenuFragment : Fragment() {

    lateinit var orcaDBHelper : OrcDBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        orcaDBHelper = OrcDBHelper(this.context!!.applicationContext)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnMapsMenu.setOnClickListener {
            val intt = Intent(this.context!!.applicationContext, MapsActivity::class.java)
            startActivity(intt)
        }

        btnMesas.setOnClickListener {
            val intt = Intent(this.context!!.applicationContext, MesaActivity::class.java)
            startActivity(intt)
        }

        btnOrcamento.setOnClickListener {
            if(edtOrc.text.isNullOrBlank()){
                Toast.makeText(this.context!!.applicationContext,"Valor inválido!", Toast.LENGTH_SHORT).show()
            }else{
                var result = orcaDBHelper.insertOrcamento(edtOrc.text.toString())
                Toast.makeText(this.context!!.applicationContext,"Orçamento inserido com sucesso: $result", Toast.LENGTH_SHORT).show()
                edtOrc.setText("")
                edtOrc.isFocusable = false;
                edtOrc.isFocusableInTouchMode = false;
                edtOrc.isClickable = false;
            }
        }
    }
}
