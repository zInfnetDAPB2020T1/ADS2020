package android.inflabnet.mytest.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Orcamento (
    @PrimaryKey(autoGenerate = true)
    var id_orcamento: Int? = null,
    var mes: Int,
    var valor: Int
)