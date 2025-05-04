package amat.laundry.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {

    @Query("SELECT * FROM Customer WHERE isDelete=0 ")
    fun getCustomerList(): Flow<List<Customer>>

    @Update
    suspend fun update(customer: Customer)

    @Insert
    suspend fun insert(customer: Customer)

    @Query(
        "UPDATE Customer " +
                "SET isDelete=1 " +
                "WHERE id=:id"
    )
    suspend fun deleteCustomer(id: String)

    @Query("SELECT * FROM Customer WHERE id=:id")
    suspend fun getCustomer(id: String): Customer

}
