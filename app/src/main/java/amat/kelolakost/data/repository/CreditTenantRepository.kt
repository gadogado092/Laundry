package amat.kelolakost.data.repository

import amat.kelolakost.data.CreditTenant
import amat.kelolakost.data.CreditTenantDao
import kotlinx.coroutines.flow.Flow

class CreditTenantRepository(private val creditTenantDao: CreditTenantDao) {
    fun getAllCreditTenant(): Flow<List<CreditTenant>> {
        return creditTenantDao.getAllCreditTenant()
    }

    suspend fun insert(creditTenant: CreditTenant) {
        creditTenantDao.insert(creditTenant)
    }

    suspend fun update(creditTenant: CreditTenant) {
        creditTenantDao.update(creditTenant)
    }

    suspend fun getTotalDebt(tenantId: String): Int {
        return creditTenantDao.getTotalDebt(tenantId)
    }

    companion object {
        @Volatile
        private var instance: CreditTenantRepository? = null

        fun getInstance(dao: CreditTenantDao): CreditTenantRepository =
            instance ?: synchronized(this) {
                CreditTenantRepository(dao).apply {
                    instance = this
                }
            }
    }
}