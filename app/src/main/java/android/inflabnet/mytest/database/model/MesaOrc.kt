package android.inflabnet.mytest.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class MesaOrc (
    @PrimaryKey(autoGenerate = true)
    var id_mesa: Int? = null,
    var id_orcamento: Int, //todo gasto em uma mesa está associado a um Orçamento, mas o transaction não funciona
    var nome_mesa: String,
    var gasto: Int,
    var mesMesa: String
){
    override fun toString(): String {
        return "Mês ${mesMesa}, Mesa ${nome_mesa}, Gastou R$ ${gasto}"
    }
}
