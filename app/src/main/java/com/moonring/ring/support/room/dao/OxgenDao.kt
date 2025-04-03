package com.moonring.ring.support.room.dao
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.moonring.ring.bean.room.CalData
import com.moonring.ring.bean.room.HeartRateData
import com.moonring.ring.bean.room.OxygenData


@Dao
interface OxygenDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(oxgenData: OxygenData): Long

    @Query("SELECT * FROM oxygen_table WHERE timestamp BETWEEN :startTime AND :endTime")
    suspend fun fetchOxygenByTimestamp(startTime: Long, endTime: Long): List<OxygenData>

    @Query("SELECT * FROM oxygen_table WHERE timestamp >= :todayStart ORDER BY timestamp DESC LIMIT 1")
    fun fetchLatestOxygenData(todayStart: Long): LiveData<OxygenData?>

    @Query("SELECT * FROM oxygen_table WHERE timestamp < :timestamp")
    suspend fun fetchOxygenDataBeforeTimestamp(timestamp: Long): List<OxygenData>

    @Update
    suspend fun update(oxgenData: OxygenData)

    @Query("UPDATE oxygen_table SET value = :value WHERE timestamp = :timestamp")
    suspend fun updateValue(timestamp: Long, value: Int)
}
