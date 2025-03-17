package amat.laundrysederhana.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CashFlowDao {

    @Update
    suspend fun update(cashFlow: CashFlow)

    @Insert
    suspend fun insert(cashFlow: CashFlow)

    @Query(
        "UPDATE Cashflow " +
                "SET isDelete=1 " +
                "WHERE id=:id"
    )
    suspend fun deleteCashFlow(id: String)

    @Query(
        "SELECT Cashflow.id AS cashFlowId, Cashflow.note AS note, Cashflow.nominal AS nominal, Cashflow.createAt AS createAt, " +
                "CashFlowCategory.id AS cashFlowCategoryId, CashFlowCategory.name AS cashFlowCategoryName " +
                "FROM CashFlow " +
                "LEFT JOIN (SELECT CashFlowCategory.id, CashFlowCategory.name FROM CashFlowCategory) AS CashFlowCategory ON Cashflow.cashFlowCategoryId = CashFlowCategory.id " +
                "WHERE isDelete=0 " +
                "AND Cashflow.createAt >= :startDate " +
                "AND Cashflow.createAt <= :endDate ORDER BY Cashflow.createAt DESC"
    )
    suspend fun getCashFlowList(startDate: String, endDate: String): List<CashFlowAndCategory>

    @Query(
        "SELECT Cashflow.id AS cashFlowId, Cashflow.note AS note, Cashflow.nominal AS nominal, Cashflow.createAt AS createAt, " +
                "CashFlowCategory.id AS cashFlowCategoryId, CashFlowCategory.name AS cashFlowCategoryName " +
                "FROM CashFlow " +
                "LEFT JOIN (SELECT CashFlowCategory.id, CashFlowCategory.name FROM CashFlowCategory) AS CashFlowCategory ON Cashflow.cashFlowCategoryId = CashFlowCategory.id " +
                "WHERE Cashflow.id =:cashFlowId"
    )
    suspend fun getCashFlow(cashFlowId: String): CashFlowAndCategory

}
