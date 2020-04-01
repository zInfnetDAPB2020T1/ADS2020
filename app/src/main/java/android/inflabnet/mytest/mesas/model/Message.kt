package android.inflabnet.mytest.mesas.model
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Message {

    constructor() //empty for firebase

    constructor(messageText: String){
        text = messageText
    }
    var text: String? = null
    var timestamp: Long = System.currentTimeMillis()
}