package android.inflabnet.mytest.database.database

import android.inflabnet.mytest.database.dao.MesaDAO
import android.inflabnet.mytest.database.dao.OrcamentoDAO
import android.inflabnet.mytest.database.model.MesaOrc
import android.inflabnet.mytest.database.model.Orcamento
import androidx.room.Database
import androidx.room.RoomDatabase

//anotação com relação de entidades(tabelas) que compõe a base
@Database(
    entities = arrayOf(
        Orcamento::class,
        MesaOrc::class
    ),
    //para notificar mudanças da base de dados do dispositivo
    version = 7
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun orcamentoDAO(): OrcamentoDAO
    abstract fun mesaDAO(): MesaDAO

}