package amat.laundry.data.repository

import amat.laundry.data.Category
import amat.laundry.data.ProductCart
import amat.laundry.data.ProductDao
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) {

    suspend fun getProductCartList(categoryId: String): List<ProductCart> {
        return productDao.getProductCartList(categoryId)
    }

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