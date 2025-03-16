package amat.laundrysederhana.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface CashFlowCategoryDao {

    @Query("SELECT * FROM CashFlowCategory WHERE isDelete=0 ")
    fun getCashFlowCategoryList(): List<CashFlowCategory>

}
