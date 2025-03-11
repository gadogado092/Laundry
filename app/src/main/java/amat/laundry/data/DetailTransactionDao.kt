package amat.laundry.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DetailTransactionDao {

    @Query("SELECT * FROM DetailTransaction")
    suspend fun getDetailTransactionList(): List<DetailTransaction>

}
