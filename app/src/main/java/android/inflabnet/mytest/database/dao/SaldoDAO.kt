package android.inflabnet.mytest.database.dao

import android.inflabnet.mytest.database.model.Saldo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
@Dao
interface SaldoDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun guardar_saldo(saldo: Saldo)

    @Query("Select sum(saldo) from  Saldo where id_Orcamento = :Orcamento_ID")
    fun buscar_ (Orcamento_ID: Int):Int

    @Query("Select saldo from  Saldo where id_mesa = :Mesa_ID")
    fun buscar (Mesa_ID: Int):Int
}