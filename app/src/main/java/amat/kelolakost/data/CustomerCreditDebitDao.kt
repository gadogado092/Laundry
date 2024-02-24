package amat.kelolakost.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CustomerCreditDebitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(customerCreditDebit: CustomerCreditDebit)

    @Query("SELECT * FROM CustomerCreditDebit WHERE isDelete=0 AND id!=0")
    suspend fun getAllCustomerCreditDebit(): List<CustomerCreditDebit>

    @Update
    suspend fun update(customerCreditDebit: CustomerCreditDebit)

}