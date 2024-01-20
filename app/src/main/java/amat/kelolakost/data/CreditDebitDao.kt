package amat.kelolakost.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CreditDebitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(creditDebit: CreditDebit)

    @Query("SELECT * FROM CreditDebit")
    fun getAllCredit(): Flow<List<CreditDebit>>

    @Update
    suspend fun update(credit: CreditDebit)

    //Add add credit debit
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cashFlow: CashFlow)

    @Transaction
    suspend fun insertCreditDebit(creditDebit: CreditDebit, cashFlow: CashFlow) {
        insert(creditDebit)
        insert(cashFlow)
    }

}