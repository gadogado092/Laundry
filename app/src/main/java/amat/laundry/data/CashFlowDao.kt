package amat.laundry.data

import amat.laundry.data.entity.Sum
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
        "SELECT Cashflow.id AS cashFlowId, Cashflow.qty AS qty, Cashflow.note AS note, Cashflow.nominal AS nominal, Cashflow.createAt AS createAt, " +
                "CashFlowCategory.id AS cashFlowCategoryId, CashFlowCategory.name AS cashFlowCategoryName, CashFlowCategory.unit As unit " +
                "FROM CashFlow " +
                "LEFT JOIN (SELECT CashFlowCategory.id, CashFlowCategory.name, CashFlowCategory.unit FROM CashFlowCategory) AS CashFlowCategory ON Cashflow.cashFlowCategoryId = CashFlowCategory.id " +
                "WHERE isDelete=0 " +
                "AND Cashflow.createAt >= :startDate " +
                "AND Cashflow.createAt <= :endDate ORDER BY Cashflow.createAt DESC"
    )
    suspend fun getCashFlowList(startDate: String, endDate: String): List<CashFlowAndCategory>

    @Query(
        "SELECT Cashflow.id AS cashFlowId, Cashflow.qty AS qty, Cashflow.note AS note, Cashflow.nominal AS nominal, Cashflow.createAt AS createAt, " +
                "CashFlowCategory.id AS cashFlowCategoryId, CashFlowCategory.unit AS unit, CashFlowCategory.name AS cashFlowCategoryName " +
                "FROM CashFlow " +
                "LEFT JOIN (SELECT CashFlowCategory.id, CashFlowCategory.name, CashFlowCategory.unit FROM CashFlowCategory) AS CashFlowCategory ON Cashflow.cashFlowCategoryId = CashFlowCategory.id " +
                "WHERE Cashflow.id =:cashFlowId"
    )
    suspend fun getCashFlow(cashFlowId: String): CashFlowAndCategory

    @Query(
        "SELECT SUM(nominal) AS total " +
                "FROM CashFlow " +
                "WHERE isDelete=0 AND cashFlowCategoryId=:cashFlowCategoryId " +
                "AND CashFlow.createAt >= :startDate " +
                "AND CashFlow.createAt <= :endDate"
    )
    suspend fun getTotalNominalCashFlow(
        cashFlowCategoryId: String,
        startDate: String,
        endDate: String
    ): Sum

    @Query(
        "SELECT SUM(Qty) AS total " +
                "FROM CashFlow " +
                "WHERE isDelete=0 AND cashFlowCategoryId=:cashFlowCategoryId " +
                "AND CashFlow.createAt >= :startDate " +
                "AND CashFlow.createAt <= :endDate"
    )
    suspend fun getTotalQtyCashFlow(
        cashFlowCategoryId: String,
        startDate: String,
        endDate: String
    ): Sum

    @Query(
        "SELECT SUM(nominal) AS total " +
                "FROM cashflow " +
                "WHERE cashflow.type='1' AND isDelete=0"
    )
    suspend fun getTotalOutcome(): Sum

    @Query(
        "SELECT SUM(totalPrice) AS total " +
                "FROM transactionlaundry " +
                "WHERE transactionlaundry.isFullPayment=1 AND isDelete=0"
    )
    suspend fun getTotalIncome(): Sum

    @Query(
        "SELECT SUM(totalPrice) AS total " +
                "FROM transactionlaundry " +
                "WHERE transactionlaundry.isFullPayment=1 AND isDelete=0 AND transactionlaundry.paymentDate >= :startDate AND transactionlaundry.paymentDate <= :endDate"
    )
    suspend fun getTotalIncomeByDate(startDate: String, endDate: String): Sum

    @Query(
        "SELECT SUM(nominal) AS total " +
                "FROM cashflow " +
                "WHERE cashflow.type='1' AND isDelete=0 AND cashflow.createAt >= :startDate AND cashflow.createAt <= :endDate"
    )
    suspend fun getTotalOutcomeByDate(startDate: String, endDate: String): Sum

}
