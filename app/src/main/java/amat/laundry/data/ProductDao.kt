package amat.laundry.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ProductDao {

    @Query("SELECT * FROM Cart")
    suspend fun getProductCartList(): List<Cart>

}
