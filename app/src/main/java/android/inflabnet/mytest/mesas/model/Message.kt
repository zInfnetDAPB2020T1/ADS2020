package android.inflabnet.mytest.mesas.model
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Message { var userChat: String? = null
                var text: String? = null
                var timestamp: String? = null
                var self: Boolean? = false

    constructor() //empty for firebase

    constructor(userC:String,messageText: String, timestampChat: String, selfMessage: Boolean){
        userChat = userC
        text = messageText
        timestamp = timestampChat
        self = selfMessage
    }

}