package amat.laundrysederhana.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {

    @Query("SELECT * FROM Customer")
    fun getCustomerList(): Flow<List<Customer>>

}
