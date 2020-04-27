package android.inflabnet.mytest.database.database

import android.inflabnet.mytest.database.dao.MesaDAO
import android.inflabnet.mytest.database.dao.OrcamentoDAO
import android.inflabnet.mytest.database.dao.SaldoDAO
import android.inflabnet.mytest.database.model.MesaOrc
import android.inflabnet.mytest.database.model.Orcamento
import android.inflabnet.mytest.database.model.Saldo
import androidx.room.Database
import androidx.room.RoomDatabase

//anotação com relação de entidades(tabelas) que compõe a base
@Database(
    entities = arrayOf(
        Orcamento::class,
        MesaOrc::class,
        Saldo::class
    ),
    //para notificar mudanças da base de dados do dispositivo
    version = 3
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun orcamentoDAO(): OrcamentoDAO
    abstract fun mesaDAO(): MesaDAO
    abstract fun saldoDAO(): SaldoDAO

}