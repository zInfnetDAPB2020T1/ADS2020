package android.inflabnet.mytest.fragments


import android.content.Context
import android.content.Intent
import android.inflabnet.mytest.R
import android.inflabnet.mytest.database.OrcDBHelper
import android.inflabnet.mytest.database.dao.OrcamentoDAO
import android.inflabnet.mytest.database.database.AppDatabase
import android.inflabnet.mytest.database.database.AppDatabaseService
import android.inflabnet.mytest.database.model.MesaOrc
import android.inflabnet.mytest.database.model.Orcamento
import android.inflabnet.mytest.maps.MapsActivity
import android.inflabnet.mytest.mesas.activity.MesaActivity
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_home_menu.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class HomeMenuFragment : Fragment() {


    private lateinit var appDatabase : AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val contFrag = activity!!.applicationContext
        appDatabase = AppDatabaseService.getInstance(contFrag)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_menu, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val month: Calendar = Calendar.getInstance()
        val currentMonth = month.get(Calendar.MONTH)
        lstGastos.text = ""
        listarGastos()

        //verify empty table
        val totRows = GetRows().execute()
        if (totRows.get() != 0){
            txtOrcamentoAtual.text = GetOrcamento().execute(currentMonth).get().valor.toString()
        }

        val mAtual = currentMonth()

        btnMapsMenu.setOnClickListener {
            val intt = Intent(this.context!!.applicationContext, MapsActivity::class.java)
            startActivity(intt)
        }

        btnMesas.setOnClickListener {
            val intt = Intent(this.context!!.applicationContext, MesaActivity::class.java)
            startActivity(intt)
        }

        //inserir orçamento mensal
        btnOrcamento.setOnClickListener {
            val contFrag = activity!!.applicationContext
            if(edtOrc.text.isNullOrBlank()){
                Toast.makeText(this.context!!.applicationContext,"Valor inválido!", Toast.LENGTH_SHORT).show()
            }else{

                //não há orçamentos - primeiro acesso
                if (totRows.get() == 0){
                    val novoOrcamento = edtOrc.text.toString()
                    InsertOrcamento().execute(Orcamento(null,currentMonth,novoOrcamento.toInt()))
                }else {
                    txtOrcamentoAtual.text =  GetOrcamento().execute(mAtual).get().valor.toString()
                    val orcamentoAtual = GetOrcamento().execute(currentMonth).get()
                    val txtTitulo = "Trocar Orçamento"
                    val dialogBuilder = AlertDialog.Builder(activity!!)
                    dialogBuilder.setMessage("Tem certeza que gostaria de trocar o orçamento ${orcamentoAtual.valor} por ${edtOrc.text} ?")
                        .setCancelable(false)
                        .setPositiveButton("Sim") { _, _ ->
                            //segue a troca de orçamentos
                            val novoOrcamento = edtOrc.text.toString()
                            orcamentoAtual.valor = novoOrcamento.toInt()
                            UpdateOrcamento().execute(orcamentoAtual)
                            edtOrc.setText("")
//                            edtOrc.isFocusable = false;
//                            edtOrc.isFocusableInTouchMode = false;
//                            edtOrc.isClickable = false;
//                            edtOrc.visibility = View.INVISIBLE
//                            btnOrcamento.visibility = View.INVISIBLE
                            txtOrcamentoAtual.setText(orcamentoAtual.valor.toString())
                            // Hide the keyboard.
                            val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            inputMethodManager.hideSoftInputFromWindow(btnOrcamento.windowToken, 0)
                        }
                        .setNeutralButton("Cancelar") { _, _ ->
                            Toast.makeText(contFrag, "Operação cancelada", Toast.LENGTH_SHORT).show()
                        }
                    val alert = dialogBuilder.create()
                    alert.setTitle(txtTitulo)
                    alert.show()
                }
            }
            val orcAtual = GetOrcamento().execute(currentMonth).get().valor
            //Toast.makeText(this.context!!.applicationContext, "Orçamento é ${orcAtual}", Toast.LENGTH_SHORT).show()
        }


    }

    private fun listarGastos() {
        ListaGastos().execute()
    }

    inner class ListaGastos:AsyncTask<Unit,Unit,Unit>(){

        override fun doInBackground(vararg params: Unit) {

            val gastos = appDatabase.mesaDAO().buscar()
            gastos.forEach {
                lstGastos.append(" Mesa: ${it.nome_mesa} , Gasto: ${it.gasto} \n" )
            }
        }
    }

    inner class UpdateOrcamento:AsyncTask<Orcamento,Unit,Unit>(){
        override fun doInBackground(vararg params: Orcamento?) {
            appDatabase.orcamentoDAO().atualiza(params[0]!!)
        }
    }

    inner class GetRows:AsyncTask<Unit,Unit,Int>(){
        override fun doInBackground(vararg params: Unit?):Int {
            val totRows = appDatabase.orcamentoDAO().getNumRows()
            return totRows
        }
    }

    inner class DeleteOrcamento:AsyncTask<Unit,Unit,Unit>(){
        override fun doInBackground(vararg params: Unit?) {
            appDatabase.orcamentoDAO().delete()
        }
    }

    inner class InsertOrcamento:AsyncTask<Orcamento,Unit,Unit>(){
        override fun doInBackground(vararg params: Orcamento?) {
            appDatabase.orcamentoDAO().guardar(params[0]!!)
        }

    }

    inner class GetOrcamento:AsyncTask<Int,Unit,Orcamento>(){
        override fun doInBackground(vararg params: Int?): Orcamento? {
            val valorMesdb = appDatabase.orcamentoDAO().buscarMes(params[0]!!)
            return valorMesdb
        }

    }

    private fun currentMonth():Int {
        val month: Calendar = Calendar.getInstance()
        val mesAtual = month.get(Calendar.MONTH)
        var mes = when (mesAtual) {
            0 -> "Jan"
            1 -> "Fev"
            2 ->  "Mar"
            3 ->  "Abr"
            4 -> "Mai"
            5 -> "Jun"
            6 -> "Jul"
            7 ->  "Ago"
            8 ->  "Set"
            9 ->  "Out"
            10 -> "Nov"
            11 ->  "Dez"
            else -> "Mês"
        }
        return mesAtual
        txtMes.setText(mes)

    }

    override fun onResume() {
        super.onResume()

    }
}
