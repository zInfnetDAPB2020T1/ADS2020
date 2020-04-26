package android.inflabnet.mytest.fragments

import android.content.Context
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
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_mesa_chat.*


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

            val contFrag = activity!!.applicationContext
            if(edtOrc.text.isNullOrBlank()){
                Toast.makeText(this.context!!.applicationContext,"Valor inválido!", Toast.LENGTH_SHORT).show()
            }else{
                val result2 :String? = orcaDBHelper.readOrcamentos()
                Toast.makeText(this.context!!.applicationContext,result2.toString(), Toast.LENGTH_SHORT).show()
                if(!result2.isNullOrBlank()) {
                    val txtTitulo = "Trocar Orçamento"
                    val dialogBuilder = AlertDialog.Builder(activity!!)
                    dialogBuilder.setMessage("Tem certeza que gostaria de trocar o orçamento $result2 por ${edtOrc.text} ?")
                            .setCancelable(false)
                            .setPositiveButton("Sim") { _, _ ->
                                //segue a troca de orçamentos
                                //deleta o que tinha de orçamento
                                orcaDBHelper.deleteOrcamento()
                                //insere o novo orçamento
                                val novoOrcamento = edtOrc.text.toString()
                                orcaDBHelper.insertOrcamento(novoOrcamento)
                                Toast.makeText(contFrag, "${orcaDBHelper.readOrcamentos()} inserido com sucesso", Toast.LENGTH_SHORT).show()
                                edtOrc.setText("")
                                edtOrc.isFocusable = false;
                                edtOrc.isFocusableInTouchMode = false;
                                edtOrc.isClickable = false;
                                edtOrc.visibility = View.INVISIBLE
                                btnOrcamento.visibility = View.INVISIBLE
                                // Hide the keyboard.
                                val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                inputMethodManager.hideSoftInputFromWindow(btnOrcamento.windowToken, 0)
                            }
                            .setNegativeButton("Não") { _, _ ->
                                Toast.makeText(contFrag, "${orcaDBHelper.readOrcamentos()} não foi removido", Toast.LENGTH_SHORT).show()
                            }
                            .setNeutralButton("Cancelar") { _, _ ->
                                Toast.makeText(contFrag, "Operação cancelada", Toast.LENGTH_SHORT).show()
                            }
                    val alert = dialogBuilder.create()
                    alert.setTitle(txtTitulo)
                    alert.show()
                }
                //Toast.makeText(this.context!!.applicationContext,"Orçamento : ${result2} inserido com sucesso", Toast.LENGTH_SHORT).show()
                else{
                    val novoOrcamento = edtOrc.text.toString()
                    orcaDBHelper.insertOrcamento(novoOrcamento)
                    Toast.makeText(contFrag, "${orcaDBHelper.readOrcamentos()} inserido com sucesso", Toast.LENGTH_SHORT).show()
                    edtOrc.setText("")
                    edtOrc.isFocusable = false;
                    edtOrc.isFocusableInTouchMode = false;
                    edtOrc.isClickable = false;
                    edtOrc.visibility = View.INVISIBLE
                    btnOrcamento.visibility = View.INVISIBLE
                    // Hide the keyboard.
                    val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(btnOrcamento.windowToken, 0)
                }
            }
        }
    }
}
