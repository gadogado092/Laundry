package amat.laundry.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CustomerDao {

    @Query("SELECT * FROM Customer WHERE isDelete=0 AND id!=0 ORDER BY name")
    suspend fun getCustomer(): List<Customer>

    @Query(
        "SELECT * FROM Customer WHERE name LIKE '%' || :value || '%' OR phoneNumber LIKE '%' || :value || '%' " +
                "ORDER BY name"
    )
    suspend fun searchCustomer(value: String): List<Customer>

//    @Query("SELECT * FROM Customer WHERE '(' || isDelete=0 AND id!=0 || ')' AND '(' || name LIKE '%' || :value || '%' OR phoneNumber LIKE '%' || :value || '%' || ')' ")

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
