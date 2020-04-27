package android.inflabnet.mytest.database.dao

import android.inflabnet.mytest.database.model.Orcamento
import android.os.FileObserver.DELETE
import androidx.room.*

@Dao
interface OrcamentoDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun guardar(orcamento: Orcamento)

    @Query("Select count(*) from Orcamento")
    fun getNumRows():Int

    @Query("DELETE FROM Orcamento")
    fun delete()

    @Query("Select * from  Orcamento")
    fun buscar ():Orcamento

    @Query("Select * from  Orcamento where valor = :valor")
    fun buscarOrc (valor: Int):Orcamento

    @Query("Select * from  Orcamento where mes = :Mes")
    fun buscarMes (Mes: Int): Orcamento

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun atualiza(orcamento: Orcamento)

}