package amat.laundry.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CashierDao {

    @Query("SELECT * FROM Cashier WHERE isDelete=0 ")
    fun getCashierList(): Flow<List<Cashier>>

}
