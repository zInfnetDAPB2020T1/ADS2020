package android.inflabnet.mytest.mesas.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Conta {
    var quem: String? = null
    var oque: String? = null
    var quanto: Int? = null
    var timestamp: String? = null
    constructor() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    constructor(username: String?, item: String?, valor: Int?, timestamp: String) {
        this.quem = username
        this.oque = item
        this.quanto = valor
        this.timestamp = timestamp
    }
}