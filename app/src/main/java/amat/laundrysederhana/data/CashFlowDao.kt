package amat.laundrysederhana.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface CashFlowDao {

    @Query("SELECT * FROM CashFlow WHERE isDelete=0 ")
    fun getCashFlowList(): List<CashFlow>

}
