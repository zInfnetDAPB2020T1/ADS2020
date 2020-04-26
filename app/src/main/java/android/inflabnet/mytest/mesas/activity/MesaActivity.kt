package android.inflabnet.mytest.mesas.activity

import android.content.Context
import android.content.Intent
import android.inflabnet.mytest.R
import android.inflabnet.mytest.mesas.adapter.MesaAdapter
import android.inflabnet.mytest.mesas.model.MembrosMesa
import android.inflabnet.mytest.mesas.model.Mesa
import android.inflabnet.mytest.mesas.model.MesaData
import android.inflabnet.mytest.mesas.model.User
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
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

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mesa)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        //mDatabaseReference = mDatabase!!.reference.child("mesas")
        mDatabaseReference = mDatabase!!.reference
        criarMesa()
        btnCriarMesa.setOnClickListener { cadastrarMesa() }
        ACTVMesas.setOnClickListener { setACTVMesas() }
    }



    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onResume() {
        super.onResume()
        setACTVMesas()
        //ACTVMesas.setText("")
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun setACTVMesas(){
        //autocomplete
        note_list_progress.visibility = View.VISIBLE
        val toReturn: ArrayList<Mesa> = arrayListOf()
        val postListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (data in dataSnapshot.children) {
                    val mesaData = data.getValue<Mesa>(Mesa::class.java)
                    //unwrap
                    val mesa = mesaData?.let { it } ?: continue
                    mesa.let { toReturn.add(it) }
                }
                note_list_progress.visibility = View.GONE
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //log error
            }
        }
        mDatabaseReference?.child("Mesas")?.addValueEventListener(postListener)

        val adapter = ArrayAdapter<Mesa>(this, android.R.layout.simple_expandable_list_item_1, toReturn)
        ACTVMesas.setAdapter(adapter)
        ACTVMesas.threshold = 1
        ACTVMesas.text.toString()

        ACTVMesas.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as Mesa

            //Toast.makeText(applicationContext, "Mesa : $selectedItem", Toast.LENGTH_SHORT).show()
            val intt = Intent(this, ContaActivity::class.java)
            val mesaData = MesaData(selectedItem.nameMesa.toString(),selectedItem.proprietarioMesa.toString())
            intt.putExtra("mesa",mesaData)
            startActivity(intt)
        }
        // Fecha o autocomplite
        ACTVMesas.setOnDismissListener {
            //Toast.makeText(applicationContext, "Sugestões fechadas", Toast.LENGTH_SHORT).show()
        }
        // Listener do layout
        rootLL.setOnClickListener {
            val text = ACTVMesas.text
            Toast.makeText(applicationContext, "Entrado : $text", Toast.LENGTH_SHORT).show()
        }
        // Listender para mudança de foco
        ACTVMesas.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
            if (b) {
                // Display the suggestion dropdown on focus
                ACTVMesas.showDropDown()
            }
        }
    }

    private fun cadastrarMesa(){
            if (etNomeMesa.text.toString().isNotEmpty()){
                gravarMesa()
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(btnCriarMesa.windowToken, 0)
            }else{
                Toast.makeText(this, "Por favor, escolha um nome para mesa!", Toast.LENGTH_SHORT).show()
            }
    }

    //pegar usuário e email
    private fun pegaUser(): User{
        //pegar o usuário
        val userEmail = mAuth?.currentUser?.email
        var user: String? = null

        if (userEmail != null) {
            if (userEmail.contains("@")) {
                user =
                    userEmail.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            } else {
                user = userEmail
            }
        }
        return User(user,userEmail)
    }

    //insere dados no firebase
    private fun gravarMesa() {
        val user = pegaUser()
        val timestamp = System.currentTimeMillis().toString()
        //inserir mesa
        mDatabaseReference?.child("Mesas")
            ?.child(timestamp)
            ?.setValue(Mesa("${etNomeMesa.text}",user.name,timestamp))
        //limpar o campo
        etNomeMesa.setText("")

    }
    //mostrar mesas no RV
    private fun criarMesa(){
        //array de mesas para o adapter do RV
        note_list_progress.visibility = View.VISIBLE
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val toReturn: ArrayList<Mesa> = ArrayList()
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
                note_list_progress.visibility = View.GONE
                setupMesaAdapter(toReturn)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                //log error
            }
        }
        mDatabaseReference?.child("Mesas")?.addValueEventListener(postListener)
    }

    private fun setupMesaAdapter(data: ArrayList<Mesa>){
        mesaRecyclerView.clearOnChildAttachStateChangeListeners()
        val linearLayoutManager = LinearLayoutManager(this)
        mesaRecyclerView.layoutManager = linearLayoutManager
        mesaRecyclerView.adapter = MesaAdapter(data, this::act)
        //scroll to bottom
        mesaRecyclerView.scrollToPosition(data.size - 1)
    }
    private fun act (data : Mesa) : Unit {
        //Toast.makeText(this, "${data.nameMesa} clicked", Toast.LENGTH_SHORT).show()
        //ao clicar ira para a tela da comanda da mesa
        val intt = Intent(this, ContaActivity::class.java)
        val mesaData = MesaData(data.nameMesa,data.proprietarioMesa.toString())
        intt.putExtra("mesa",mesaData)
        startActivity(intt)
    }

}