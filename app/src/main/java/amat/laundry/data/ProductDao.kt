package amat.laundry.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ProductDao {

    @Query(
        "SELECT Product.id AS productId, Product.name AS productName, Product.price AS productPrice, " +
                "Category.name AS categoryName, Category.unit AS unit, " +
                "Cart.qty AS qty " +
                "FROM Product " +
                "LEFT JOIN (SELECT Category.id, Category.name, Category.unit FROM Category) AS Category ON Product.categoryId = Category.id " +
                "LEFT JOIN (SELECT Cart.productId, Cart.qty FROM Cart) AS Cart ON Product.id = Cart.productId " +
                "WHERE Product.isDelete=0 AND Product.categoryId=:categoryId " +
                "ORDER BY Product.name ASC"
    )
    suspend fun getProductCartList(categoryId: String): List<ProductCart>

}
