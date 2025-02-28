package amat.laundry.data

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


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: List<User>)


//    @Transaction
//    suspend fun prosesInsertRestore(
//        dataUser: List<User>,
//        dataKost: List<Kost>,
//        dataUnitStatus: List<UnitStatus>,
//        dataUnitType: List<UnitType>,
//        dataUnit: List<Unit>,
//        dataTenant: List<Tenant>,
//        dataCashFlow: List<CashFlow>,
//        dataBooking: List<Booking>,
//        dataCreditTenant: List<CreditTenant>,
//        dataCreditDebit: List<CreditDebit>,
//        dataCustomerCreditDebit: List<CustomerCreditDebit>
//    ) {
//        //DELETE
//        deleteAllUser()
//        deleteAllKost()
//        deleteAllUnitStatus()
//        deleteAllUnitType()
//        deleteAllUnit()
//        deleteAllTenant()
//        deleteAllCashFlow()
//        deleteAllBooking()
//        deleteAllCreditTenant()
//        deleteAllCreditDebit()
//        deleteAllCustomerCreditDebit()
//
//        //INSERT
//        insertUser(dataUser)
//        insertKost(dataKost)
//        insertUnitStatus(dataUnitStatus)
//        insertUnitType(dataUnitType)
//        insertUnit(dataUnit)
//        insertTenant(dataTenant)
//        insertCashFlow(dataCashFlow)
//        insertBooking(dataBooking)
//        insertCreditTenant(dataCreditTenant)
//        insertCreditDebit(dataCreditDebit)
//        insertCustomerCreditDebit(dataCustomerCreditDebit)
//
//    }
}