package amat.laundry.data.repository

import amat.laundry.data.Category
import amat.laundry.data.CategoryDao
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {

    fun getAllCategory(): Flow<List<Category>> {
        return categoryDao.getCategoryList()
    }


    companion object {
        @Volatile
        private var instance: CategoryRepository? = null

        fun getInstance(dao: CategoryDao): CategoryRepository =
            instance ?: synchronized(this) {
                CategoryRepository(dao).apply {
                    instance = this
                }
            }
    }

}