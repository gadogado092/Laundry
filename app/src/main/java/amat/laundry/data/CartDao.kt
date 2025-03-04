package amat.laundry.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface CartDao {

    @Query("SELECT * FROM Cart")
    suspend fun getCartList(): List<Cart>

}
