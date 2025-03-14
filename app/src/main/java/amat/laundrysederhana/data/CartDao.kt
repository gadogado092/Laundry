package amat.laundrysederhana.data

import amat.laundrysederhana.data.entity.Sum
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CartDao {

    @Query(
        "SELECT Cart.productId AS productId, Cart.qty AS qty, Cart.note AS note, " +
                "Category.name AS categoryName " +
                "FROM Cart " +
                "LEFT JOIN (SELECT Product.id, Product.categoryId, Product.isDelete FROM Product) AS Product ON Product.id = Cart.productId " +
                "LEFT JOIN (SELECT Category.id, Category.name FROM Category) AS Category ON Product.categoryId = Category.id " +
                "WHERE Product.isDelete=0 AND Product.categoryId=:categoryId "
    )
    suspend fun getCartList(categoryId: String): List<CartCategory>

    @Query(
        "SELECT Cart.productId AS productId, Product.name AS productName, Product.Price AS productPrice, Cart.totalPrice AS productTotalPrice, Cart.qty AS qty, Cart.note AS note, " +
                "Category.id AS categoryId, Category.name AS categoryName, Category.unit AS unit " +
                "FROM Cart " +
                "LEFT JOIN (SELECT Product.id, Product.name, Product.Price, Product.categoryId, Product.isDelete FROM Product) AS Product ON Product.id = Cart.productId " +
                "LEFT JOIN (SELECT Category.id, Category.name, Category.unit FROM Category) AS Category ON Product.categoryId = Category.id " +
                "WHERE Product.isDelete=0"
    )
    suspend fun getCartList(): List<ProductCart>

    @Query(
        "SELECT Cart.productId AS productId, Cart.qty AS qty, Cart.note AS note, " +
                "Category.name AS categoryName " +
                "FROM Cart " +
                "LEFT JOIN (SELECT Product.id, Product.categoryId, Product.isDelete FROM Product) AS Product ON Product.id = Cart.productId " +
                "LEFT JOIN (SELECT Category.id, Category.name FROM Category) AS Category ON Product.categoryId = Category.id " +
                "WHERE Product.isDelete=0 AND Product.id=:productId "
    )
    suspend fun getCartDetail(productId: String): CartCategory

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cart: Cart)

    @Delete
    suspend fun delete(cart: Cart)

    @Query(
        "DELETE FROM Cart WHERE productId=:productId"
    )
    suspend fun delete(productId: String)

    @Query("DELETE FROM Cart")
    suspend fun deleteAllCart()

    @Query(
        "SELECT SUM(totalPrice) AS total " +
                "FROM Cart"
    )
    suspend fun getTotalPriceCart(): Sum

    @Query(
        "SELECT COUNT(productId) AS total " +
                "FROM Cart"
    )
    suspend fun getTotalDataCart(): Sum

}
