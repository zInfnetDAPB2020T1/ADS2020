package android.inflabnet.mytest.mesas.activity

import android.content.Intent
import android.inflabnet.mytest.R
import android.inflabnet.mytest.mesas.adapter.MessageAdapter
import android.inflabnet.mytest.mesas.model.MesaData
import android.inflabnet.mytest.mesas.model.Message
import android.inflabnet.mytest.login.LoginActivity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_mesa_chat.*

class MesaChatActivity : AppCompatActivity() {

    //Firebase references
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mesa_chat)

        //Recebendo os Valores da activity MesaActivity
        val mesaData = intent.getSerializableExtra("mesa") as MesaData
        txtNomedaMesa.text = mesaData.nameMesa.toString()
        txtProp.text = mesaData.proprietarioMesa.toString()
        val pathStr = mesaData.nameMesa.toString()+"chat"

        Toast.makeText(this,pathStr,Toast.LENGTH_SHORT).show()

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child(pathStr.toString())

        createFirebaseListener(pathStr)
        btnEnviarMsg.setOnClickListener { setupSendButton(pathStr) }

    }

    //ao clicar para enviar mensagem
    private fun setupSendButton(pathStr: String) {
        if (mainChatEditText2.text.toString().isNotEmpty()){
            sendData(pathStr)
        }else{
            Toast.makeText(this, "Por favor, escreva uma mensagem!", Toast.LENGTH_SHORT).show()
        }
    }

    //enviar dados para firebase
    private fun sendData(pathStr: String){
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
                setValue(Message("$user - ${mainChatEditText2.text.toString()}"))

        }else {
            val intt = Intent(this, LoginActivity::class.java)
            startActivity(intt)
        }

        //limpar a entrada de dados
        mainChatEditText2.setText("")
    }

    private fun createFirebaseListener(pathStr : String){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val toReturn: ArrayList<Message> = ArrayList();

                for(data in dataSnapshot.children){
                    val messageData = data.getValue<Message>(Message::class.java)

                    //unwrap
                    val message = messageData?.let { it } ?: continue

                    toReturn.add(message)
                }

                //sort so newest at bottom
                toReturn.sortBy { message ->
                    message.timestamp
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
    private fun setupAdapter(data: ArrayList<Message>){
        val linearLayoutManager = LinearLayoutManager(this)
        mainActivityRecyclerView.layoutManager = linearLayoutManager
        mainActivityRecyclerView.adapter = MessageAdapter(data) {
            Toast.makeText(this, "${it.text} clicked", Toast.LENGTH_SHORT).show()
        }

        //scroll to bottom
        mainActivityRecyclerView.scrollToPosition(data.size - 1)
    }

}
