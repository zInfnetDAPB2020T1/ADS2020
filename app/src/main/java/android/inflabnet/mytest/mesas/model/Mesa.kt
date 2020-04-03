package android.inflabnet.mytest.mesas.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Mesa {
    var nameMesa: String? = null
    var proprietarioMesa: String? = null
    var timestamp: String? = null
    constructor() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    constructor(mesa: String?, user: String?,timestamp: String) {
        this.nameMesa = mesa
        this.proprietarioMesa = user
        this.timestamp = timestamp
    }
}