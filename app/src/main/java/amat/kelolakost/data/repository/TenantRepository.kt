package amat.kelolakost.data.repository

import amat.kelolakost.data.Tenant
import amat.kelolakost.data.TenantHome
import amat.kelolakost.data.TenantDao
import android.util.Log
import kotlinx.coroutines.flow.Flow

class TenantRepository(private val tenantDao: TenantDao) {
    suspend fun getAllTenantHome(status: String = ""): List<TenantHome> {
        Log.d("saya", "getAllTenantHome $status")
        return when (status) {
            "1" -> {
                tenantDao.getAllTenantHomeCheckIn()
            }

            "0" -> {
                tenantDao.getAllTenantHomeCheckOut()
            }

            else -> {
                tenantDao.getAllTenantHome()
            }
        }
    }

    fun getDetail(id: String): Flow<Tenant> {
        return tenantDao.getDetail(id)
    }

    suspend fun getTenantCheckOut(): List<Tenant>{
        return tenantDao.getTenantCheckOut()
    }

    suspend fun insertTenant(tenant: Tenant) {
        tenantDao.insert(tenant)
    }

    suspend fun updateTenant(tenant: Tenant) {
        tenantDao.update(tenant)
    }

    companion object {
        @Volatile
        private var instance: TenantRepository? = null

        fun getInstance(dao: TenantDao): TenantRepository =
            instance ?: synchronized(this) {
                TenantRepository(dao).apply {
                    instance = this
                }
            }
    }
}