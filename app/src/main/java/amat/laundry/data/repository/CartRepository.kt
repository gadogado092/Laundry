package amat.laundry.data.repository

import amat.laundry.data.Cart
import amat.laundry.data.CartDao
import amat.laundry.data.Category
import amat.laundry.data.CategoryDao
import kotlinx.coroutines.flow.Flow

class CartRepository(private val cartDao: CartDao) {

    suspend fun insert(cart: Cart) {
        cartDao.insert(cart)
    }


    companion object {
        @Volatile
        private var instance: CartRepository? = null

        fun getInstance(dao: CartDao): CartRepository =
            instance ?: synchronized(this) {
                CartRepository(dao).apply {
                    instance = this
                }
            }
    }

}