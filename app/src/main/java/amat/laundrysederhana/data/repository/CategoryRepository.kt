package amat.laundrysederhana.data.repository

import amat.laundrysederhana.data.Category
import amat.laundrysederhana.data.CategoryDao
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {

    suspend fun insert(category: Category) {
        categoryDao.insert(category)
    }

    fun getAllCategory(): Flow<List<Category>> {
        return categoryDao.getCategoryList()
    }

    suspend fun getCategory(): List<Category> {
        return categoryDao.getCategory()
    }

    suspend fun getCategory(id: String): Category {
        return categoryDao.getCategory(id)
    }

    suspend fun update(category: Category) {
        categoryDao.update(category)
    }

    suspend fun deleteCategory(id: String){
        categoryDao.deleteCategory(id)
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