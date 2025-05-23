package amat.laundry.data

import amat.laundry.data.entity.Sum
import androidx.room.Dao
import androidx.room.Query

@Dao
interface DetailTransactionDao {

    @Query(
        "SELECT * FROM DetailTransaction " +
                "WHERE transactionId=:transactionId"
    )
    suspend fun getDetailTransactionList(transactionId: String): List<DetailTransaction>

    @Query(
        "SELECT SUM(totalPrice) AS total " +
                "FROM DetailTransaction " +
                "WHERE isDelete=0 AND categoryId=:categoryId " +
                "AND DetailTransaction.createAt >= :startDate " +
                "AND DetailTransaction.createAt <= :endDate"
    )
    suspend fun getTotalPriceDetailTransaction(
        categoryId: String,
        startDate: String,
        endDate: String
    ): Sum

    @Query(
        "SELECT SUM(qty) AS total " +
                "FROM DetailTransaction " +
                "WHERE isDelete=0 AND categoryId=:categoryId " +
                "AND DetailTransaction.createAt >= :startDate " +
                "AND DetailTransaction.createAt <= :endDate"
    )
    suspend fun getTotalQtyDetailTransaction(
        categoryId: String,
        startDate: String,
        endDate: String
    ): Sum

}
