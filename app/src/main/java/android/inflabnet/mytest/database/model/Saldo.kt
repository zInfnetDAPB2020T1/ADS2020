package android.inflabnet.mytest.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Saldo (
    @PrimaryKey(autoGenerate = true)
    var id_saldo: Int,
    var saldo: Int,
    var id_orcamento: Int,
    var id_mesa: Int
)