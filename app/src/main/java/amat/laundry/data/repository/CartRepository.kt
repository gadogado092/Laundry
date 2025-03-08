package amat.laundry.data.repository

import amat.laundry.data.Cart
import amat.laundry.data.CartCategory
import amat.laundry.data.CartDao
import amat.laundry.data.Category
import amat.laundry.data.CategoryDao
import amat.laundry.data.ProductCategory
import amat.laundry.data.entity.Sum
import kotlinx.coroutines.flow.Flow

class CartRepository(private val cartDao: CartDao) {

    suspend fun insert(cart: Cart) {
        cartDao.insert(cart)
    }

    suspend fun delete(cart: Cart) {
        cartDao.delete(cart)
    }

    suspend fun delete(productId: String) {
        cartDao.delete(productId)
    }

    suspend fun deleteAllCart() {
        cartDao.deleteAllCart()
    }

    suspend fun getCartList(categoryId: String): List<CartCategory> {
        return cartDao.getCartList(categoryId)
    }

    suspend fun getCartDetail(productId: String): CartCategory {
        return cartDao.getCartDetail(productId)
    }

    suspend fun getTotalPriceCart(): Sum{
        return cartDao.getTotalPriceCart()
    }

    suspend fun getTotalDataCart(): Sum{
        return cartDao.getTotalDataCart()
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