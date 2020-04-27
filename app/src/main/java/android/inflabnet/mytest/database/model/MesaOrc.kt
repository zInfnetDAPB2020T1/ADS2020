package android.inflabnet.mytest.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class MesaOrc (
    @PrimaryKey(autoGenerate = true)
    var id_mesa: Int? = null,
    var nome_mesa: String,
    var gasto: Int
)