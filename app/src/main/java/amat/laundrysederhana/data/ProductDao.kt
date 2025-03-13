package amat.laundrysederhana.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProductDao {

    @Update
    suspend fun update(product: Product)

    @Insert
    suspend fun insert(product: Product)

    @Query(
        "UPDATE Product " +
                "SET isDelete=1 " +
                "WHERE id=:id"
    )
    suspend fun deleteProduct(id: String)

    @Query(
        "SELECT Product.id AS productId, Product.name AS productName, Product.price AS productPrice, " +
                "Category.id AS categoryId, Category.name AS categoryName, Category.unit AS unit " +
                "FROM Product " +
                "LEFT JOIN (SELECT Category.id, Category.name, Category.unit FROM Category) AS Category ON Product.categoryId = Category.id " +
                "WHERE Product.isDelete=0 AND Product.categoryId=:categoryId " +
                "ORDER BY Product.name ASC"
    )
    suspend fun getProductList(categoryId: String): List<ProductCategory>

    @Query(
        "SELECT Product.id AS productId, Product.name AS productName, Product.price AS productPrice, " +
                "Category.id AS categoryId, Category.name AS categoryName, Category.unit AS unit " +
                "FROM Product " +
                "LEFT JOIN (SELECT Category.id, Category.name, Category.unit FROM Category) AS Category ON Product.categoryId = Category.id " +
                "WHERE Product.isDelete=0 " +
                "ORDER BY Product.name ASC"
    )
    suspend fun getProductList(): List<ProductCategory>

    @Query(
        "SELECT Product.id AS productId, Product.name AS productName, Product.price AS productPrice, " +
                "Category.id AS categoryId, Category.name AS categoryName, Category.unit AS unit " +
                "FROM Product " +
                "LEFT JOIN (SELECT Category.id, Category.name, Category.unit FROM Category) AS Category ON Product.categoryId = Category.id " +
                "WHERE Product.isDelete=0 AND Product.id=:productId " +
                "ORDER BY Product.name ASC"
    )
    suspend fun getProductCategoryDetail(productId: String): ProductCategory

}
