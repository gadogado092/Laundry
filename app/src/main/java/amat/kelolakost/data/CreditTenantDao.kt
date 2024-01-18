package amat.kelolakost.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface CreditTenantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(creditTenant: CreditTenant)

    @Query(
        "SELECT Tenant.id AS tenantId, Tenant.name AS tenantName, Tenant.numberPhone AS tenantNumberPhone, SUM(CreditTenant.remainingDebt) AS total " +
                "FROM CreditTenant " +
                "LEFT JOIN (SELECT Tenant.id, Tenant.name, Tenant.numberPhone FROM Tenant) AS Tenant ON CreditTenant.tenantId = Tenant.id " +
                "WHERE CreditTenant.isDelete=0 AND CreditTenant.id !=0 " +
                "GROUP BY CreditTenant.tenantId " +
                "ORDER BY total DESC"
    )
    suspend fun getAllCreditTenant(): List<CreditTenantHome>

    @Query(
        "SELECT * FROM CreditTenant WHERE tenantId=:tenantId AND isDelete=0 ORDER BY remainingDebt DESC"
    )
    suspend fun getAllCreditTenant(tenantId: String): List<CreditTenant>

    @Query(
        "SELECT CreditTenant.id AS creditTenantId, CreditTenant.note AS note, Tenant.id AS tenantId, Tenant.name AS tenantName, Tenant.numberPhone AS tenantNumberPhone, CreditTenant.remainingDebt AS remainingDebt, " +
                " Unit.id AS unitId, Kost.id AS kostId " +
                "FROM CreditTenant " +
                "LEFT JOIN (SELECT Tenant.id, Tenant.name, Tenant.numberPhone, Tenant.unitId FROM Tenant) AS Tenant ON CreditTenant.tenantId = Tenant.id " +
                "LEFT JOIN (SELECT Unit.id, Unit.kostId FROM Unit) AS Unit ON Unit.id = Tenant.unitId " +
                "LEFT JOIN (SELECT Kost.id FROM Kost) AS Kost ON Kost.id = Unit.kostId " +
                "WHERE CreditTenant.isDelete=0 AND CreditTenant.id =:creditTenantId"
    )
    suspend fun getDetailCreditTenant(creditTenantId: String): CreditTenantDetail

    @Query(
        "SELECT Tenant.id AS tenantId, Tenant.name AS tenantName, Tenant.numberPhone AS tenantNumberPhone, SUM(CreditTenant.remainingDebt) AS total " +
                "FROM CreditTenant " +
                "LEFT JOIN (SELECT Tenant.id, Tenant.name, Tenant.numberPhone FROM Tenant) AS Tenant ON CreditTenant.tenantId = Tenant.id " +
                "WHERE CreditTenant.isDelete=0 AND Tenant.id =:tenantId " +
                "GROUP BY CreditTenant.tenantId " +
                "ORDER BY total ASC"
    )
    suspend fun getCreditTenant(tenantId: String): CreditTenantHome

    @Update
    suspend fun update(creditTenant: CreditTenant)

    @Query("SELECT SUM(remainingDebt) FROM CreditTenant WHERE tenantId=:tenantId AND isDelete=0")
    suspend fun getTotalDebt(tenantId: String): Int

    //Payment Debt
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cashFlow: CashFlow)

    @Query("UPDATE CreditTenant SET remainingDebt=:remainingDebt WHERE id=:creditTenantId")
    suspend fun updateRemainingDebt(creditTenantId: String, remainingDebt: Int)

    @Transaction
    suspend fun payDebt(cashFlow: CashFlow, remainingDebt: Int) {
        //update remainingDebt
        updateRemainingDebt(cashFlow.creditTenantId, remainingDebt)
        //insert cashflow
        insert(cashFlow)
    }

    //DELETE AREA
    @Query("UPDATE CreditTenant SET isDelete=1 WHERE id=:creditTenantId")
    suspend fun deleteCreditTenant(creditTenantId: String)

}