package amat.laundry.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CartDao {

    @Query("SELECT Cart.productId AS productId, Cart.qty AS qty, Cart.note AS note, " +
            "Category.name AS categoryName " +
            "FROM Cart " +
            "LEFT JOIN (SELECT Product.id, Product.categoryId, Product.isDelete FROM Product) AS Product ON Product.id = Cart.productId " +
            "LEFT JOIN (SELECT Category.id, Category.name FROM Category) AS Category ON Product.categoryId = Category.id " +
            "WHERE Product.isDelete=0 AND Product.categoryId=:categoryId ")
    suspend fun getCartList(categoryId: String): List<CartCategory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cart: Cart)

    @Delete
    suspend fun delete(cart: Cart)

    @Query("DELETE FROM Cart")
    suspend fun deleteAllCart()

}
