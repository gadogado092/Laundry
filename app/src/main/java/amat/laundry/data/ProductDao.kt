package amat.laundry.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ProductDao {

    @Query(
        "SELECT Product.id AS productId, Product.name AS productName " +
                "FROM Product " +
                "WHERE Product.isDelete=0 AND Product.categoryId=:categoryId " +
                "ORDER BY Product.name ASC"
    )
    suspend fun getProductCartList(categoryId: String): List<ProductCart>

}
