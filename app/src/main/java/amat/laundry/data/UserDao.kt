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

    @Query("UPDATE User SET printerName=:printerName, printerAddress=:printerAddress  WHERE id=:userId")
    suspend fun printerSelected(userId: String, printerName: String, printerAddress: String)

    //RESTORE Data
    @Query("DELETE FROM User")
    suspend fun deleteAllUser()


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: List<User>)

    //NEW USER AREA
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(categoryList: List<Category>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatus(statusList: List<LaundryStatus>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(productList: List<Product>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer)

    @Transaction
    suspend fun transactionInsertNewUser(
        user: User,
        statusList: List<LaundryStatus>,
        categoryList: List<Category>,
        productList: List<Product>,
        customer: Customer
    ) {
        //INSERT
        insert(user)
        insertStatus(statusList)
        insertCategory(categoryList)
        insertProduct(productList)
        insertCustomer(customer)
    }
}