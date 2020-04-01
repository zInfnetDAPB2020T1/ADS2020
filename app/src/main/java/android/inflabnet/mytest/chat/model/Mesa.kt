package android.inflabnet.mytest.chat.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Mesa {
    var nameMesa: String? = null
    var proprietarioMesa: String? = null
    var timestamp: Long? = null
    constructor() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    constructor(mesa: String?, user: String?) {
        this.nameMesa = mesa
        this.proprietarioMesa = user
        var timestamp: Long = System.currentTimeMillis()
    }
}