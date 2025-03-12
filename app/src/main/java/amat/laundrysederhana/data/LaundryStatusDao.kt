package amat.laundrysederhana.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface LaundryStatusDao {

    @Query("SELECT * FROM LaundryStatus")
    suspend fun getStatusList(): List<LaundryStatus>

}
