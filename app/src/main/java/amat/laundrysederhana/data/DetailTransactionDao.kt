package amat.laundrysederhana.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface DetailTransactionDao {

    @Query(
        "SELECT * FROM DetailTransaction " +
                "WHERE transactionId=:transactionId"
    )
    suspend fun getDetailTransactionList(transactionId: String): List<DetailTransaction>

}
