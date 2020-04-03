package android.inflabnet.mytest.mesas.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Mesa {
    var nameMesa: String? = null
    var proprietarioMesa: String? = null
    var timestamp: Long? = null
    var participantes: MutableList<User> = mutableListOf(User())
    constructor() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    constructor(mesa: String?, user: String?, participantes: MutableList<User>) {
        this.nameMesa = mesa
        this.proprietarioMesa = user
        var timestamp: Long = System.currentTimeMillis()
        this.participantes = participantes
    }
}