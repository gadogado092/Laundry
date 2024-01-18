package amat.kelolakost.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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
        "SELECT * FROM CreditTenant WHERE tenantId=:tenantId ORDER BY remainingDebt DESC"
    )
    suspend fun getAllCreditTenant(tenantId: String): List<CreditTenant>

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

    @Query("SELECT SUM(remainingDebt) FROM CreditTenant WHERE tenantId=:tenantId")
    suspend fun getTotalDebt(tenantId: String): Int

}