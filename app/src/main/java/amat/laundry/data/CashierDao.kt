package amat.laundry.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CashierDao {

    @Query("SELECT * FROM Cashier WHERE isDelete=0 AND id!=0")
    suspend fun getCashier(): List<Cashier>

    @Update
    suspend fun update(cashier: Cashier)

    @Insert
    suspend fun insert(cashier: Cashier)

    @Query(
        "UPDATE Cashier " +
                "SET isDelete=1 " +
                "WHERE id=:id"
    )
    suspend fun deleteCashier(id: String)

    @Query("SELECT * FROM Cashier WHERE id=:id")
    suspend fun getCashier(id: String): Cashier

}
