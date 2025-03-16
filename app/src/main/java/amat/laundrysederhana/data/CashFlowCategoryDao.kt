package amat.laundrysederhana.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CashFlowCategoryDao {

    @Query("SELECT * FROM CashFlowCategory WHERE isDelete=0")
    fun getCashFlowCategoryList(): Flow<List<CashFlowCategory>>

    @Query("SELECT * FROM CashFlowCategory WHERE isDelete=0")
    suspend fun getCashFlowCategory(): List<CashFlowCategory>

    @Query("SELECT * FROM CashFlowCategory WHERE isDelete=0 AND id!=0")
    suspend fun getCashFlowCategory0(): List<CashFlowCategory>

    @Query("SELECT * FROM CashFlowCategory WHERE id=:id")
    suspend fun getCashFlowCategory(id: String): CashFlowCategory

    @Update
    suspend fun update(category: CashFlowCategory)

    @Insert
    suspend fun insert(category: CashFlowCategory)

    @Query(
        "UPDATE CashFlowCategory " +
                "SET isDelete=1 " +
                "WHERE id=:id"
    )
    suspend fun deleteCategory(id: String)

}
