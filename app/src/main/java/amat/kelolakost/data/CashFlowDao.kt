package amat.kelolakost.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CashFlowDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cashFlow: CashFlow)

    @Query("SELECT * FROM CashFlow")
    fun getAllCashFlow(): Flow<List<CashFlow>>

    @Update
    suspend fun update(cashFlow: CashFlow)

}