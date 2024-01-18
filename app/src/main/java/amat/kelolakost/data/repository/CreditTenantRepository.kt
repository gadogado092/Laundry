package amat.kelolakost.data.repository

import amat.kelolakost.data.CashFlow
import amat.kelolakost.data.CreditTenant
import amat.kelolakost.data.CreditTenantDao
import amat.kelolakost.data.CreditTenantDetail
import amat.kelolakost.data.CreditTenantHome

class CreditTenantRepository(private val creditTenantDao: CreditTenantDao) {
    suspend fun getAllCreditTenant(): List<CreditTenantHome> {
        return creditTenantDao.getAllCreditTenant()
    }

    suspend fun getCreditTenant(tenantId: String): CreditTenantHome {
        return creditTenantDao.getCreditTenant(tenantId)
    }

    suspend fun getAllCreditTenant(tenantId: String): List<CreditTenant> {
        return creditTenantDao.getAllCreditTenant(tenantId)
    }

    suspend fun getDetailCreditTenant(creditTenantId: String): CreditTenantDetail {
        return creditTenantDao.getDetailCreditTenant(creditTenantId)
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

    suspend fun payDebt(cashFlow: CashFlow, remainingDebt: Int) {
        creditTenantDao.payDebt(cashFlow, remainingDebt)
    }

    suspend fun deleteCreditTenant(creditTenantId: String) {
        creditTenantDao.deleteCreditTenant(creditTenantId)
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