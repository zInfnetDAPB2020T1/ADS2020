package android.inflabnet.mytest.mesas.activity

import android.content.Intent
import android.inflabnet.mytest.R
import android.inflabnet.mytest.database.OrcDBHelper
import android.inflabnet.mytest.login.LoginActivity
import android.inflabnet.mytest.mesas.adapter.ContaAdapter
import android.inflabnet.mytest.mesas.model.Conta
import android.inflabnet.mytest.mesas.model.MesaData
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_conta_chat.*
import kotlinx.android.synthetic.main.item_consumido.view.*

class ContaChatActivity : AppCompatActivity() {

    lateinit var orcaDBHelper : OrcDBHelper

    //Firebase references
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    private var user: String? = null
    lateinit var pathStr: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conta_chat)
        //inicializando o DB local
        orcaDBHelper = OrcDBHelper(this)

        //Recebendo os Valores da activity MesaActivity
        val mesaData = intent.getSerializableExtra("mesa") as MesaData
        txtMesaConta.text = mesaData.nameMesa.toString()
        txtProprit.text = mesaData.proprietarioMesa.toString()
        pathStr = mesaData.nameMesa.toString()+"conta"
        //Toast.makeText(this,pathStr.toString(),Toast.LENGTH_SHORT).show()

        //instanciando o banco
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child(pathStr)

        //chama o chat
        btnChatinho.setOnClickListener {
            val intt = Intent(this, MesaChatActivity::class.java)
            val mesaData = MesaData(mesaData.nameMesa.toString(),mesaData.proprietarioMesa.toString())
            intt.putExtra("mesa",mesaData)
            startActivity(intt)
        }

        //swipe para remover um item
        val itemtouchhelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item = viewHolder.itemView
                val qmCons = item.txtQuem.toString()
                val itemCons = item.txtOq.toString()
                val valor = Integer.parseInt(item.txtQuanto.getText().toString())
                val ts = item.txtTimestamp.toString()
                val itemClass = Conta(qmCons,itemCons,valor,ts)
                removeItem(itemClass)
                rvConta.adapter
            }

        })
        itemtouchhelper.attachToRecyclerView(rvConta)

        pegarUser()
        createFirebaseListener(pathStr)
        contaListener(pathStr)
        btnOk.setOnClickListener { setupSendButton(pathStr) }


    }
    private fun pegarUser(){
        //pegar o usuário
        val userEmail = mAuth?.currentUser?.email
        //val user: String
        if (userEmail != null) {
            if (userEmail.contains("@")) {
                user =
                    userEmail.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            } else {
                user = userEmail
            }
        }else {
            val intt = Intent(this, LoginActivity::class.java)
            startActivity(intt)
        }
    }

    //ao clicar para enviar mensagem
    private fun setupSendButton(pathStr: String) {
        if (!edtItem.text.toString().isEmpty() || edtValor.text.toString().isEmpty()){
            sendData(pathStr, edtItem.text.toString(),edtValor.text.toString())
        }else{
            Toast.makeText(this, "Por favor, colocar um item e seu valor", Toast.LENGTH_SHORT).show()
        }
    }

    //enviar dados para firebase
    private fun sendData(pathStr: String, item:String, valor: String){
        val itemTimestamp = System.currentTimeMillis().toString()
        mDatabaseReference?.
            child(pathStr)?.
            child(itemTimestamp)?.
            setValue(Conta(user, item,valor.toInt(), itemTimestamp))
        //limpar a entrada de dados
        edtItem.setText("")
        edtValor.setText("")
    }
    //listener para itens da comanda
    private fun createFirebaseListener(pathStr : String){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val toReturn: ArrayList<Conta> = ArrayList();
                for(data in dataSnapshot.children){
                    val contaData = data.getValue<Conta>(Conta::class.java)
                    //unwrap
                    val conta = contaData?.let { it } ?: continue
                    toReturn.add(conta)
                }
                //sort so newest at bottom
                toReturn.sortBy { conta ->
                    conta.timestamp
                }
                setupAdapter(toReturn)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                //log error
            }
        }
        mDatabaseReference?.child(pathStr)?.addValueEventListener(postListener)

    }
    //listener para valor da conta pessoal
    private fun contaListener(pathStr: String){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val toReturn: ArrayList<Conta> = ArrayList();

                for(data in dataSnapshot.children){
                    val contaData = data.getValue<Conta>(Conta::class.java)
                    //unwrap
                    val conta = contaData?.let { it } ?: continue
                    //montando o array
                    toReturn.add(conta)
                }
                //sort so newest at bottom
                toReturn.sortBy { conta ->
                    conta.timestamp
                }
                setupTxtView(toReturn)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                //log error
            }
        }
        mDatabaseReference?.child(pathStr)?.addValueEventListener(postListener)
    }
    //colocar os Txts de valores da conta
    private fun setupTxtView(data: ArrayList<Conta>){
        val orcStr = orcaDBHelper.readOrcamentos().last()
        val orcamento = orcStr.toDouble()
        var totEu: Double = 0.0
        val tot = data.sumBy { conta ->
            conta.quanto!!.toInt()
        }
        for (conta in data){
            if(conta.quem == user){
                totEu += conta.quanto!!
            }
        }
        txtTotConta.text = tot.toString()
        if (totEu == 0.0){
            txtTotEu.visibility = View.GONE
            txtTotEuText.visibility = View.GONE
        }else {
            txtTotEu.text = totEu.toString()
        }
        val percentage = (totEu/orcamento!!) *100.0
//        Toast.makeText(this,"totEu: ${totEu.toString()}",Toast.LENGTH_SHORT).show()
//        Toast.makeText(this,"orcamento: ${orcamento.toString()}",Toast.LENGTH_SHORT).show()
        if (percentage < 75.0){
            txtTotEu.setTextColor(ContextCompat.getColor(applicationContext, R.color.green))
            txtTotEuText.setTextColor(ContextCompat.getColor(applicationContext, R.color.green))
        }else if (percentage < 90.0){
            txtTotEu.setTextColor(ContextCompat.getColor(applicationContext, R.color.yellow))
            txtTotEuText.setTextColor(ContextCompat.getColor(applicationContext, R.color.yellow))
        }else{
            txtTotEu.setTextColor(ContextCompat.getColor(applicationContext, R.color.red))
            txtTotEuText.setTextColor(ContextCompat.getColor(applicationContext, R.color.red))
        }
    }

    //mostrar os dados
    private fun setupAdapter(data: ArrayList<Conta>) {
        val linearLayoutManager = LinearLayoutManager(this)
        rvConta.layoutManager = linearLayoutManager
        rvConta.adapter = ContaAdapter(data) {
            Toast.makeText(this, "${it.timestamp} clicked", Toast.LENGTH_SHORT).show()
            removeItem(it)
        }
            //scroll to bottom
            rvConta.scrollToPosition(data.size - 1)

    }

    private fun removeItem(item: Conta){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val toReturn: ArrayList<Conta> = ArrayList();

                for(data in dataSnapshot.children){
                    val contaData = data.getValue<Conta>(Conta::class.java)
                    //unwrap
                    val conta = contaData?.let { it } ?: continue
                    //montando o array
                    if(item.timestamp != conta.timestamp) {
                        toReturn.add(conta)
                    }else{
                        conta.timestamp?.let { mDatabaseReference?.child(pathStr)?.child(it)
                            ?.removeValue() }
                        Toast.makeText(applicationContext," ${conta.oque} removido",Toast.LENGTH_SHORT).show()
                    }
                }
                //sort so newest at bottom
                toReturn.sortBy { conta ->
                    conta.timestamp
                }
                setupAdapter(toReturn)
                setupTxtView(toReturn)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                //log error
            }
        }
        mDatabaseReference?.child(pathStr)?.addValueEventListener(postListener)


    }
}
