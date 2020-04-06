package android.inflabnet.mytest.mesas.activity

import android.content.Intent
import android.inflabnet.mytest.R
import android.inflabnet.mytest.database.OrcDBHelper
import android.inflabnet.mytest.login.LoginActivity
import android.inflabnet.mytest.mesas.adapter.ContaAdapter
import android.inflabnet.mytest.mesas.model.Conta
import android.inflabnet.mytest.mesas.model.MembrosMesa
import android.inflabnet.mytest.mesas.model.MesaData
import android.inflabnet.mytest.mesas.model.fechouConta
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

        pegarUser()
        createFirebaseListener(pathStr)
        contaListener(pathStr)
        btnOk.setOnClickListener { setupSendButton(pathStr) }
        membrosLista(mesaData.nameMesa)
        btnFinalizar.setOnClickListener { finalizar() }
        jaFinalizados()
    }

    //não precisa falar. Ok pega o usuário que está logado
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

    //finalizar a conta individual
    private fun finalizar() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Tem certeza que gostaria de pagar sua parte?")
                .setCancelable(false)
                .setPositiveButton("Sim"){_, _ ->
                    //segue a deleção dos itens de quem fechou a conta e a soma do total dos seus itens
                    val postListener = object : ValueEventListener {
                        var totalConta: Int = 0
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val toReturn: ArrayList<Conta> = ArrayList();
                            for(data in dataSnapshot.children){
                                val contaData = data.getValue<Conta>(Conta::class.java)
                                val ts = data.getValue<Conta>(Conta::class.java)?.timestamp
                                //unwrap
                                val conta = contaData?.let { it } ?: continue
                                //montando o array removendo os itens de quem está finalizando a comanda
                                if(conta.quem == user) {
                                    conta.let {
                                        if (ts != null) {
                                            mDatabaseReference?.child(pathStr)?.child(ts)?.removeValue()
                                            totalConta += (conta.quanto!!)
                                        }
                                    }

                                }else{
                                    toReturn.add(conta)
                                }
                            }
                            //coloca user que saiu no txt dos que já correram
                            //txtFinalizado.append("${user} : ${totalConta}\n").toString()
                            //não deixa colocar mais produtos
                            btnOk.visibility = View.GONE
                            //Toast.makeText(this@ContaActivity," ${totalConta}  a pagar",Toast.LENGTH_SHORT).show()
                            //criar um grupo no Fbase aPagar de quem já fechou e seus valores
                            user?.let { aPagar(it,totalConta) }
                            //sort so newest at bottom
                            toReturn.sortBy { conta ->
                                conta.timestamp
                            }
                            setupAdapter(toReturn)
                            //jaFinalizados() //atializa o txt
                        }
                        override fun onCancelled(databaseError: DatabaseError) {
                            //log error
                        }
                    }
                    //mDatabaseReference?.child(pathStr)?.addValueEventListener(postListener)
                    mDatabaseReference?.child(pathStr)?.addListenerForSingleValueEvent(postListener)


                    //remover user do grupo membros
                    val membroListener = object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            //varre a lista membros do FBase procurando o nome da mesa
                            //se encontrar remover o membro no Fbase
                            dataSnapshot.children.forEach{
                                if(it.getValue<MembrosMesa>(MembrosMesa::class.java)?.nomeMesa.toString() == txtMesaConta.text.toString() &&
                                        it.getValue<MembrosMesa>(MembrosMesa::class.java)?.membro.toString() == user) {
                                    val id = it.getValue<MembrosMesa>(MembrosMesa::class.java)?.id.toString()
                                    mDatabaseReference?.child("membros")?.child(id)?.removeValue()
                                    //txtMembros.append("${it.getValue<MembrosMesa>(MembrosMesa::class.java)?.membro.toString()}\n")
                                }
                            }
                        }
                        override fun onCancelled(p0: DatabaseError) {
                            Toast.makeText(applicationContext, "Errroooo",Toast.LENGTH_SHORT ).show()
                        }
                    }
                    mDatabaseReference!!.child("membros").addListenerForSingleValueEvent(membroListener)

                }
                .setNegativeButton("Não") { _, _ ->
                    Toast.makeText(this,"Ok, a noite é uma criança!",Toast.LENGTH_SHORT).show()
                }
                .setNeutralButton("Cancelar") {_, _ ->
                    Toast.makeText(this,"Operação cancelada",Toast.LENGTH_SHORT).show()
                }
        val alert = dialogBuilder.create()
        alert.setTitle("Fechar conta?")
        alert.show()
    }
    //listener para quem já finalizou

    //cria um child no Fbase "aPagar" para guardar quem já finalizou a comanda
    //função chamda depois de clicar em finalizar
    private fun aPagar(userP: String, totConta: Int) {
        val mesaP = txtMesaConta.text.toString()
        //atiualizar firebase com os que fecharam a conta
        //referencia do caminho
        val dbRefe = mDatabaseReference!!.child("aPagar")
        //gerar a key
        val apTimestamp = System.currentTimeMillis().toString()
        //montar o objeto
        val aPagarObj =fechouConta(userP,mesaP,totConta,apTimestamp)
        dbRefe.child(apTimestamp).setValue(aPagarObj)
    }

    //popular o txt com quem já saiu do grupo
    private fun jaFinalizados() {
        val japagouListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                txtFinalizado.setText("")
                //varre a lista membros do FBase procurando o nome da mesa
                //se encontrar no grupo dos aPagar adicionar ao TXT
                dataSnapshot.children.forEach{
                    if(it.getValue<fechouConta>(fechouConta::class.java)?.mesa.toString() == txtMesaConta.text.toString()) {
                        txtFinalizado.append(" ${it.getValue<fechouConta>(fechouConta::class.java)?.user.toString()} : ${it.getValue<fechouConta>(fechouConta::class.java)?.totConta.toString()}\n").toString()
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(applicationContext, "Errroooo",Toast.LENGTH_SHORT ).show()
            }
        }
        mDatabaseReference!!.child("aPagar").addValueEventListener(japagouListener)
    }

    //atualiza os membros da mesa em txtMembros
    private fun membrosLista(mesa: String) {
        val membroListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                txtMembros.setText("")
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
        mDatabaseReference!!.child("membros").addValueEventListener(membroListener)
    }

    //ao clicar para enviar um item para comanda e entrar no grupo da mesa, caso já não esteja
    private fun setupSendButton(pathStr: String) {
        if (edtItem.text.toString().isEmpty() && edtValor.text.toString().isEmpty()){
            Toast.makeText(this, "Por favor, inserir um item e seu valor.", Toast.LENGTH_SHORT).show()
        }else if (edtItem.text.toString().isEmpty()){
            Toast.makeText(this, "Por favor, inserir um item.", Toast.LENGTH_SHORT).show()
        } else if (edtValor.text.toString().isEmpty()){
            Toast.makeText(this, "Por favor, colocar o valor do item", Toast.LENGTH_SHORT).show()
        }
        else{
            //entrar no grupo da mesa
            //aparece a conta
            if (user.toString() in txtMembros.text){
                Toast.makeText(this,"Cliente já está na mesa", Toast.LENGTH_SHORT).show()
            }else {
                //atiualizar firebase com nome da mesa e novo membro
                val mesa = user?.let { it1 -> MembrosMesa(txtMesaConta.text.toString(), it1) }
                val key = mDatabaseReference!!.child("membros").push().key
                if (mesa != null) {
                    if (key != null) {
                        mesa.id = key
                    }
                }
                if (key != null) {
                    mDatabaseReference!!.child("membros").child(key).setValue(mesa)
                    //coloca o user no grupo membros da mesa
                    //txtMembros.append(user)

                }
            }
            //enviar dados do item consumido para banco
            sendData(pathStr, edtItem.text.toString(),edtValor.text.toString())
        }
    }

    //envia dados para firebase
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
        var orcStr: String? = null
        orcStr = try{
            orcaDBHelper.readOrcamentos().last()
        }catch (e: Exception){
            "500000.0"
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
        val percentage = (totEu/ orcamento) *100.0
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

    //mostrar os dados e deletar no evento toque
    private fun setupAdapter(data: ArrayList<Conta>) {
        val linearLayoutManager = LinearLayoutManager(this)
        rvConta.layoutManager = linearLayoutManager
        //scroll to bottom
        rvConta.scrollToPosition(data.size - 1)
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
    }

    //remove item na comanda
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
