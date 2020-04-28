package android.inflabnet.mytest.database.dao

import android.inflabnet.mytest.database.model.MesaOrc
import androidx.room.*

@Dao
interface MesaDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun guardar(mesa: MesaOrc)

    @Query("Select * from  MesaOrc")
    fun buscar ():Array<MesaOrc>

    @Query("delete from  MesaOrc")
    fun delete ()

    @Query("Select count(*) from MesaOrc ")
    fun getNumRows():Int

    @Query("Select sum(gasto) from MesaOrc where mesMesa = :mes")
    fun gastosAtuais(mes: String):Int
}