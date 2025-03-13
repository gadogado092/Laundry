package amat.laundrysederhana.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM Category WHERE isDelete=0")
    fun getCategoryList(): Flow<List<Category>>

    @Query("SELECT * FROM Category WHERE isDelete=0")
    suspend fun getCategory(): List<Category>

    @Query("SELECT * FROM Category WHERE id=:id")
    suspend fun getCategory(id: String): Category

    @Update
    suspend fun update(category: Category)

    @Insert
    suspend fun insert(category: Category)

    @Query(
        "UPDATE Category " +
                "SET isDelete=1 " +
                "WHERE id=:id"
    )
    suspend fun deleteCategory(id: String)

}
