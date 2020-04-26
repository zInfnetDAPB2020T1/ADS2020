package android.inflabnet.mytest.database

import android.provider.BaseColumns

class DBContract {
    class OsOrcamentos : BaseColumns {
        companion object {
            val TABLE_NAME = "orcamento"
            val COLUMN_VALOR = "quanto"
        }
    }
}