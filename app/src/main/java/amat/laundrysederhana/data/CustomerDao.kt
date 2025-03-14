package amat.laundrysederhana.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {

    @Query("SELECT * FROM Customer WHERE isDelete=0 ")
    fun getCustomerList(): Flow<List<Customer>>

}
