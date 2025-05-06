package amat.laundry.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface CashierDao {

    @Query("SELECT * FROM Cashier WHERE isDelete=0 AND id!=0")
    suspend fun getCashier(): List<Cashier>

    @Query("SELECT * FROM Cashier WHERE isDelete=0")
    suspend fun getAllCashier(): List<Cashier>

    @Query("SELECT * FROM Cashier WHERE isLastUsed=1 AND isDelete=0")
    suspend fun getCashierLastUsed(): List<Cashier>

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

    @Query(
        "UPDATE Cashier " +
                "SET isLastUsed=1 " +
                "WHERE id=:idLastUsed"
    )
    suspend fun setCashierIsLastUsed(idLastUsed: String)

    @Query(
        "UPDATE Cashier " +
                "SET isLastUsed=0 " +
                "WHERE id!=:id AND isDelete=0"
    )
    suspend fun setOtherCashierIsNotLastUsed(id: String)

    @Transaction
    suspend fun transactionSetCashierLastUsed(id: String) {
        setOtherCashierIsNotLastUsed(id)
        setCashierIsLastUsed(id)
    }

    @Query("SELECT * FROM Cashier WHERE id=:id")
    suspend fun getCashier(id: String): Cashier

}
