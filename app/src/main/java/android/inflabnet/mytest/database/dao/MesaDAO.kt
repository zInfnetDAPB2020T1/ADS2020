package android.inflabnet.mytest.database.dao

import android.inflabnet.mytest.database.model.MesaOrc
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
@Dao
interface MesaDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun guardar(mesa: MesaOrc)

    @Query("Select * from  MesaOrc")
    fun buscar ():Array<MesaOrc>

    @Query("delete from  MesaOrc")
    fun delete ()

}