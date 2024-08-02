package amat.kelolakost.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Query("SELECT * FROM User")
    fun getAllUser(): Flow<List<User>>

    @Query("SELECT * FROM User")
    suspend fun getUser(): List<User>

    @Update
    suspend fun update(user: User)

    @Query("SELECT * FROM User LIMIT 1")
    fun getDetail(): Flow<User>

    @Query("SELECT * FROM User LIMIT 1")
    suspend fun getProfile(): User

    @Query("UPDATE User SET `limit`=:newLimit, `key`=:newKey  WHERE id=:userId")
    suspend fun extendApp(userId: String, newLimit: String, newKey: String)

    //RESTORE Data
    @Query("DELETE FROM User")
    suspend fun deleteAllUser()

    @Query("DELETE FROM Kost")
    suspend fun deleteAllKost()

    @Query("DELETE FROM UnitStatus")
    suspend fun deleteAllUnitStatus()

    @Query("DELETE FROM UnitType")
    suspend fun deleteAllUnitType()

    @Query("DELETE FROM Unit")
    suspend fun deleteAllUnit()

    @Query("DELETE FROM Tenant")
    suspend fun deleteAllTenant()

    @Query("DELETE FROM CashFlow")
    suspend fun deleteAllCashFlow()

    @Query("DELETE FROM Booking")
    suspend fun deleteAllBooking()

    @Query("DELETE FROM CreditTenant")
    suspend fun deleteAllCreditTenant()

    @Query("DELETE FROM CreditDebit")
    suspend fun deleteAllCreditDebit()

    @Query("DELETE FROM CustomerCreditDebit")
    suspend fun deleteAllCustomerCreditDebit()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: List<User>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKost(kost: List<Kost>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnitStatus(unitStatus: List<UnitStatus>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnitType(unitType: List<UnitType>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnit(unit: List<Unit>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTenant(tenant: List<Tenant>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCashFlow(cashFlow: List<CashFlow>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: List<Booking>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreditTenant(creditTenant: List<CreditTenant>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreditDebit(creditDebit: List<CreditDebit>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomerCreditDebit(creditDebit: List<CustomerCreditDebit>)

    @Transaction
    suspend fun prosesInsertRestore(
        dataUser: List<User>,
        dataKost: List<Kost>,
        dataUnitStatus: List<UnitStatus>,
        dataUnitType: List<UnitType>,
        dataUnit: List<Unit>,
        dataTenant: List<Tenant>,
        dataCashFlow: List<CashFlow>,
        dataBooking: List<Booking>,
        dataCreditTenant: List<CreditTenant>,
        dataCreditDebit: List<CreditDebit>,
        dataCustomerCreditDebit: List<CustomerCreditDebit>
    ) {
        //DELETE
        deleteAllUser()
        deleteAllKost()
        deleteAllUnitStatus()
        deleteAllUnitType()
        deleteAllUnit()
        deleteAllTenant()
        deleteAllCashFlow()
        deleteAllBooking()
        deleteAllCreditTenant()
        deleteAllCreditDebit()
        deleteAllCustomerCreditDebit()

        //INSERT
        insertUser(dataUser)
        insertKost(dataKost)
        insertUnitStatus(dataUnitStatus)
        insertUnitType(dataUnitType)
        insertUnit(dataUnit)
        insertTenant(dataTenant)
        insertCashFlow(dataCashFlow)
        insertBooking(dataBooking)
        insertCreditTenant(dataCreditTenant)
        insertCreditDebit(dataCreditDebit)
        insertCustomerCreditDebit(dataCustomerCreditDebit)

    }
}