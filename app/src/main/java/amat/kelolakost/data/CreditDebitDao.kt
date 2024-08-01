package amat.kelolakost.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface CreditDebitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(creditDebit: CreditDebit)

    @Query(
        "SELECT CreditDebit.id AS creditDebitId, CreditDebit.note AS note, CreditDebit.remaining AS remaining, CreditDebit.status AS status, CreditDebit.dueDate AS dueDate, " +
                "CustomerCreditDebit.name AS customerCreditDebitName, CustomerCreditDebit.numberPhone AS customerCreditDebitNumberPhone " +
                "FROM CreditDebit " +
                "LEFT JOIN (SELECT CustomerCreditDebit.id, CustomerCreditDebit.name, CustomerCreditDebit.numberPhone FROM CustomerCreditDebit) AS CustomerCreditDebit ON CreditDebit.customerCreditDebitId = CustomerCreditDebit.id " +
                "WHERE CreditDebit.isDelete=0 AND CreditDebit.customerCreditDebitId!=0 " +
                "ORDER BY CreditDebit.remaining DESC, CreditDebit.dueDate ASC "
    )
    suspend fun getAllCreditDebit(): List<CreditDebitHome>

    @Query(
        "SELECT CreditDebit.id AS creditDebitId, CreditDebit.note AS note, CreditDebit.remaining AS remaining, CreditDebit.status AS status, CreditDebit.dueDate AS dueDate, " +
                "CustomerCreditDebit.name AS customerCreditDebitName, CustomerCreditDebit.numberPhone AS customerCreditDebitNumberPhone " +
                "FROM CreditDebit " +
                "LEFT JOIN (SELECT CustomerCreditDebit.id, CustomerCreditDebit.name, CustomerCreditDebit.numberPhone FROM CustomerCreditDebit) AS CustomerCreditDebit ON CreditDebit.customerCreditDebitId = CustomerCreditDebit.id " +
                "WHERE CreditDebit.isDelete=0 AND CreditDebit.id=:creditDebitId " +
                "ORDER BY CreditDebit.dueDate ASC"
    )
    suspend fun getDetailCreditDebit(creditDebitId: String): CreditDebitHome

    @Query(
        "SELECT * FROM CreditDebit"
    )
    suspend fun getListCreditDebit(): List<CreditDebit>

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

    //pay credit debit
    @Query("UPDATE CreditDebit SET remaining=:remaining, dueDate=:dueDate  WHERE id=:creditDebitId")
    suspend fun updateCreditDebit(creditDebitId: String, remaining: Int, dueDate: String)

    @Transaction
    suspend fun payCreditDebit(cashFlow: CashFlow, remaining: Int, dueDate: String) {
        insert(cashFlow)
        updateCreditDebit(cashFlow.creditDebitId, remaining, dueDate)
    }

    //DELETE AREA
    @Query("UPDATE CreditDebit SET isDelete=1 WHERE id=:creditDebitId")
    suspend fun deleteCreditDebit(creditDebitId: String)

}