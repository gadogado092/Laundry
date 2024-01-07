package amat.kelolakost.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerCreditDebitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(customerCreditDebit: CustomerCreditDebit)

    @Query("SELECT * FROM CustomerCreditDebit")
    fun getAllCustomerCreditDebit(): Flow<List<CustomerCreditDebit>>

    @Update
    suspend fun update(customerCreditDebit: CustomerCreditDebit)

}