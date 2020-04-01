package android.inflabnet.mytest.mesas.activity

import android.content.Intent
import android.inflabnet.mytest.R
import android.inflabnet.mytest.login.LoginActivity
import android.inflabnet.mytest.mesas.adapter.ContaAdapter
import android.inflabnet.mytest.mesas.model.Conta
import android.inflabnet.mytest.mesas.model.MesaData
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_conta_chat.*

class ContaChatActivity : AppCompatActivity() {

    //Firebase references
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conta_chat)

        //Recebendo os Valores da activity MesaActivity
        val mesaData = intent.getSerializableExtra("mesa") as MesaData
        txtMesaConta.text = mesaData.nameMesa.toString()
        txtProprit.text = mesaData.proprietarioMesa.toString()
        val pathStr = mesaData.nameMesa.toString()+"conta"

        //chama o chat
        btnChatinho.setOnClickListener {
            val intt = Intent(this, MesaChatActivity::class.java)
            val mesaData = MesaData(mesaData.nameMesa.toString(),mesaData.proprietarioMesa.toString())
            intt.putExtra("mesa",mesaData)
            startActivity(intt)
        }
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child(pathStr)

        createFirebaseListener(pathStr)
        btnOk.setOnClickListener { setupSendButton(pathStr) }

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
        //pegar o usuário
        val userEmail = mAuth?.currentUser?.email
        val user: String
        if (userEmail != null) {
            if (userEmail.contains("@")) {
                user =
                    userEmail.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            } else {
                user = userEmail
            }
            mDatabaseReference?.
                child(pathStr.toString())?.
                child(java.lang.String.valueOf(System.currentTimeMillis()))?.
                setValue(Conta(user, item,valor.toInt()))

        }else {
            val intt = Intent(this, LoginActivity::class.java)
            startActivity(intt)
        }
        //limpar a entrada de dados
        edtItem.setText("")
        edtValor.setText("")
    }

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

    //mostrar os dados
    private fun setupAdapter(data: ArrayList<Conta>){
        val linearLayoutManager = LinearLayoutManager(this)
        rvConta.layoutManager = linearLayoutManager
        rvConta.adapter = ContaAdapter(data) {
            Toast.makeText(this, "${it.quem} clicked", Toast.LENGTH_SHORT).show()
        }

        //scroll to bottom
        rvConta.scrollToPosition(data.size - 1)
    }
}
