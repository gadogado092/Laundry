package amat.laundry.data.repository

import amat.laundry.data.CategoryDao

class CategoryRepository(private val categoryDao: CategoryDao) {

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