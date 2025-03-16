package amat.laundrysederhana.data.repository

import amat.laundrysederhana.data.CashFlowCategory
import amat.laundrysederhana.data.CashFlowCategoryDao
import kotlinx.coroutines.flow.Flow

class CashFlowCategoryRepository(private val cashFlowCategoryDao: CashFlowCategoryDao) {

    suspend fun insert(category: CashFlowCategory) {
        cashFlowCategoryDao.insert(category)
    }

    fun getAllCategory(): Flow<List<CashFlowCategory>> {
        return cashFlowCategoryDao.getCashFlowCategoryList()
    }

    suspend fun getCategory(): List<CashFlowCategory> {
        return cashFlowCategoryDao.getCashFlowCategory()
    }

    suspend fun getCashFlowCategory0(): List<CashFlowCategory> {
        return cashFlowCategoryDao.getCashFlowCategory0()
    }

    suspend fun getCategory(id: String): CashFlowCategory {
        return cashFlowCategoryDao.getCashFlowCategory(id)
    }

    suspend fun update(category: CashFlowCategory) {
        cashFlowCategoryDao.update(category)
    }

    suspend fun deleteCategory(id: String) {
        cashFlowCategoryDao.deleteCategory(id)
    }

    companion object {
        @Volatile
        private var instance: CashFlowCategoryRepository? = null

        fun getInstance(cashFlowCategoryDao: CashFlowCategoryDao): CashFlowCategoryRepository =
            instance ?: synchronized(this) {
                CashFlowCategoryRepository(cashFlowCategoryDao).apply {
                    instance = this
                }
            }
    }
}