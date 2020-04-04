package android.inflabnet.mytest.mesas.activity

import android.content.Intent
import android.inflabnet.mytest.R
import android.inflabnet.mytest.database.OrcDBHelper
import android.inflabnet.mytest.login.LoginActivity
import android.inflabnet.mytest.mesas.adapter.ContaAdapter
import android.inflabnet.mytest.mesas.model.Conta
import android.inflabnet.mytest.mesas.model.MembrosMesa
import android.inflabnet.mytest.mesas.model.MesaData
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_conta_chat.*
import kotlinx.android.synthetic.main.item_consumido.view.*

class ContaActivity : AppCompatActivity() {

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
        txtMesaConta.text = mesaData.nameMesa
        txtProprit.text = mesaData.proprietarioMesa
        pathStr = mesaData.nameMesa+"conta"
        //Toast.makeText(this,pathStr.toString(),Toast.LENGTH_SHORT).show()

        //instanciando o banco
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference

        //chama o chat
        btnChatinho.setOnClickListener {
            val intt = Intent(this, MesaChatActivity::class.java)
            val mesaData = MesaData(mesaData.nameMesa.toString(),mesaData.proprietarioMesa.toString())
            intt.putExtra("mesa",mesaData)
            startActivity(intt)
        }
        //apaga a lista de produtos até entrar na conta
        btnEntrar.setOnClickListener {
            //entrar no grupo da mesa
            //aparece a conta
            btnEntrar.visibility = View.GONE
            if (user.toString() in txtMembros.text){
                Toast.makeText(this,"Cliente já está na mesa", Toast.LENGTH_SHORT).show()
            }else {
                //coloca o user na tela
                txtMembros.append(user)
                //atiualizar firebase com nome da mesa e membro
                val mesa = user?.let { it1 -> MembrosMesa(mesaData.nameMesa, it1) }
                val key = mDatabaseReference!!.child("membros").push().key
                if (mesa != null) {
                    if (key != null) {
                        mesa.id = key
                    }
                }
                if (key != null) {
                    mDatabaseReference!!.child("membros").child(key).setValue(mesa)
                }
            }
        }

        pegarUser()
        createFirebaseListener(pathStr)
        contaListener(pathStr)
        btnOk.setOnClickListener { setupSendButton(pathStr) }
        membrosLista(mesaData.nameMesa)
    }
    //atualiza os membros da mesa
    private fun membrosLista(mesa: String) {
        val membroListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //varre a lista membros do FBase procurando o nome da mesa
                //se encontrar adiciona o membro no txtMembros
                dataSnapshot.children.forEach{
                    if(it.getValue<MembrosMesa>(MembrosMesa::class.java)?.nomeMesa.toString() == mesa )
                        txtMembros.append("${it.getValue<MembrosMesa>(MembrosMesa::class.java)?.membro.toString()}\n")
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(applicationContext, "Errroooo",Toast.LENGTH_SHORT ).show()
            }
        }
        mDatabaseReference!!.child("membros").addListenerForSingleValueEvent(membroListener)
    }

    //não precisa falar
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

    //ao clicar para enviar um item para comanda
    private fun setupSendButton(pathStr: String) {
        if (edtItem.text.toString().isEmpty() && edtValor.text.toString().isEmpty()){
            Toast.makeText(this, "Por favor, inserir um item e seu valor.", Toast.LENGTH_SHORT).show()
        }else if (edtItem.text.toString().isEmpty()){
            Toast.makeText(this, "Por favor, inserir um item.", Toast.LENGTH_SHORT).show()
        } else if (edtValor.text.toString().isEmpty()){
            Toast.makeText(this, "Por favor, colocar o valor do item", Toast.LENGTH_SHORT).show()
        }
        else{
            sendData(pathStr, edtItem.text.toString(),edtValor.text.toString())
        }
    }

    //enviar dados para firebase
    private fun sendData(pathStr: String, item:String, valor: String){
        val itemTimestamp = System.currentTimeMillis().toString()
        val conta = Conta(user, item,valor.toInt(), itemTimestamp)
        mDatabaseReference?.
            child(pathStr)?.
            child(itemTimestamp)?.
            setValue(conta)
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

            }
            override fun onCancelled(databaseError: DatabaseError) {
                //log error
            }
        }
        mDatabaseReference?.child(pathStr)?.addValueEventListener(postListener)
    }
    //colocar os Txts de valores da conta
    private fun setupTxtView(data: ArrayList<Conta>){
        var orcStr: String = "500000.0"
        if(!orcaDBHelper.readOrcamentos().last().isBlank()) {
            orcStr = orcaDBHelper.readOrcamentos().last()
        }
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
            txtTotEu.visibility = View.VISIBLE
            txtTotEuText.visibility = View.VISIBLE
            txtTotEu.text = totEu.toString()
        }
        val percentage = (totEu/orcamento!!) *100.0
        //Toast.makeText(this,"totEu: ${totEu.toString()}",Toast.LENGTH_SHORT).show()
       // Toast.makeText(this,"orcamento: ${orcamento.toString()}",Toast.LENGTH_SHORT).show()
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

    //mostrar os dados e deletar no toque
    private fun setupAdapter(data: ArrayList<Conta>) {
        val linearLayoutManager = LinearLayoutManager(this)
        rvConta.layoutManager = linearLayoutManager
        rvConta.adapter = ContaAdapter(data) {
            val txtTitulo = "${it.oque} de ${it.quem} no valor de ${it.quanto}?"
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("Tem certeza que gostaria de deletar $txtTitulo ?")
                .setCancelable(false)
                .setPositiveButton("Sim"){_, _ ->
                    //segue a deleção do item
                    removeItem(it)
                    Toast.makeText(this, "${it.oque} removido da comanda", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Não") { _, _ ->
                    Toast.makeText(this,"${it.oque} não foi removido",Toast.LENGTH_SHORT).show()
                }
                .setNeutralButton("Cancelar") {_, _ ->
                    Toast.makeText(this,"Operação cancelada",Toast.LENGTH_SHORT).show()
                }
                val alert = dialogBuilder.create()
                alert.setTitle("Deletar Item da Comanda")
                alert.show()
        }
        setupTxtView(data)
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

            }
            override fun onCancelled(databaseError: DatabaseError) {
                //log error
            }
        }
        mDatabaseReference?.child(pathStr)?.addValueEventListener(postListener)
    }

}
