package android.inflabnet.mytest.mesas.activity

import android.content.Intent
import android.inflabnet.mytest.R
import android.inflabnet.mytest.mesas.adapter.MesaAdapter
import android.inflabnet.mytest.mesas.model.Mesa
import android.inflabnet.mytest.mesas.model.MesaData
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_mesa.*
import kotlinx.android.synthetic.main.mesa_item.*

class MesaActivity : AppCompatActivity() {

    //Firebase references
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mesa)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("mesas")

        criarMesa()
        btnCriarMesa.setOnClickListener {cadastrarMesa() }

    }

    private fun cadastrarMesa(){
            if (!etNomeMesa.text.toString().isEmpty()){
                gravarMesa()
            }else{
                Toast.makeText(this, "Por favor, escreva uma mensagem!", Toast.LENGTH_SHORT).show()
            }

    }
    private fun gravarMesa() {
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
        //pegar nome da mesa
            mDatabaseReference?.child("mesas")
                ?.child(java.lang.String.valueOf(System.currentTimeMillis()))
                ?.setValue(Mesa("${etNomeMesa.text}", user))
        }
        //limpar o campo
        etNomeMesa.setText("")
    }

    private fun criarMesa(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val toReturn: ArrayList<Mesa> = ArrayList();
                for(data in dataSnapshot.children){
                    val mesaData = data.getValue<Mesa>(Mesa::class.java)
                    //unwrap
                    val mesa = mesaData?.let { it } ?: continue
                    toReturn.add(mesa)
                }
                //sort so newest at bottom
                toReturn.sortBy { mesa ->
                    mesa.timestamp
                }
                setupMesaAdapter(toReturn)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                //log error
            }
        }
        mDatabaseReference?.child("mesas")?.addValueEventListener(postListener)
    }
    private fun setupMesaAdapter(data: ArrayList<Mesa>){
        val linearLayoutManager = LinearLayoutManager(this)
        mesaRecyclerView.layoutManager = linearLayoutManager
        mesaRecyclerView.adapter = MesaAdapter(data, this::act)
        //scroll to bottom
        mesaRecyclerView.scrollToPosition(data.size - 1)
    }
    private fun act (data : Mesa) : Unit {
        Toast.makeText(this, "${data.nameMesa} clicked", Toast.LENGTH_SHORT).show()
        //ao clicar ira para a tela do chat da referida mesa
        //val intt = Intent(this, MesaChatActivity::class.java)
        val intt = Intent(this, ContaChatActivity::class.java)
        val mesaData = MesaData(data.nameMesa.toString(),data.proprietarioMesa.toString())
        intt.putExtra("mesa",mesaData)
        startActivity(intt)
    }
}