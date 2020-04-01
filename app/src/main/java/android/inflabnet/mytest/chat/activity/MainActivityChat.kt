package android.inflabnet.mytest.chat.activity

import android.content.Intent
import android.inflabnet.mytest.R
import android.inflabnet.mytest.chat.adapter.MessageAdapter
import android.inflabnet.mytest.chat.model.Message
import android.inflabnet.mytest.login.LoginActivity
import android.inflabnet.mytest.maps.MapsActivity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main_chat.*

class MainActivityChat : AppCompatActivity() {

    //Firebase references
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_chat)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("messages")
        createFirebaseListener()
        mainActivitySendButton.setOnClickListener { setupSendButton() }
    }

    /**
     * OnClick action for the send button
     */
    private fun setupSendButton() {

            if (!mainActivityEditText.text.toString().isEmpty()){
                sendData()
            }else{
                Toast.makeText(this, "Por favor, escreva uma mensagem!", Toast.LENGTH_SHORT).show()
            }

    }
    /**
     * Send data to firebase
     */
    private fun sendData(){
        //pegar o usuário
        val userEmail = mAuth?.currentUser?.email
        val user: String
        if (userEmail != null) {
            if (userEmail!!.contains("@")) {
                user =
                    userEmail.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            } else {
                user = userEmail
            }
            mDatabaseReference?.
                child("messages")?.
                child(java.lang.String.valueOf(System.currentTimeMillis()))?.
                setValue(Message("$user - ${mainActivityEditText.text.toString()}"))

        }else {
            var intt = Intent(this, LoginActivity::class.java)
            startActivity(intt)
        }

        //clear the text
        mainActivityEditText.setText("")
    }

    private fun createFirebaseListener(){
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
        mDatabaseReference?.child("messages")?.addValueEventListener(postListener)
    }
    /**
     * Once data is here - display it
     */
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
