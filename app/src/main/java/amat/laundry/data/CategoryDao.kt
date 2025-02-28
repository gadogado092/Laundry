package amat.laundry.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface CategoryDao {

    @Query("SELECT * FROM Category")
    suspend fun getCategoryList(): List<Category>

}
