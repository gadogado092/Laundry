package amat.laundry.data.repository

import amat.laundry.data.ProductDao

class ProductRepository(private val productDao: ProductDao) {

    companion object {
        @Volatile
        private var instance: ProductRepository? = null

        fun getInstance(dao: ProductDao): ProductRepository =
            instance ?: synchronized(this) {
                ProductRepository(dao).apply {
                    instance = this
                }
            }
    }

}