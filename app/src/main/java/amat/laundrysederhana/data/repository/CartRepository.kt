package amat.laundrysederhana.data.repository

import amat.laundrysederhana.data.Cart
import amat.laundrysederhana.data.CartCategory
import amat.laundrysederhana.data.CartDao
import amat.laundrysederhana.data.ProductCart
import amat.laundrysederhana.data.entity.Sum

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

    suspend fun getCartList(): List<ProductCart> {
        return cartDao.getCartList()
    }

    suspend fun getCartDetail(productId: String): CartCategory {
        return cartDao.getCartDetail(productId)
    }

    suspend fun getTotalPriceCart(): Sum {
        return cartDao.getTotalPriceCart()
    }

    suspend fun getTotalDataCart(): Sum {
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