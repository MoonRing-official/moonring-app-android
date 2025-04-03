package com.moonring.ring.support.room.dao
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.moonring.ring.bean.room.CalData
import com.moonring.ring.bean.room.HeartRateData

@Dao
interface HeartRateDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend  fun insert(heartRateData: HeartRateData): Long

    @Query("SELECT * FROM heartrate_table WHERE timestamp BETWEEN :startTime AND :endTime")
    suspend fun fetchHeartRateDataByTimestamp(startTime: Long, endTime: Long): List<HeartRateData>
    @Query("SELECT * FROM heartrate_table WHERE timestamp < :timestamp")
    suspend fun fetchHeartRateDataBeforeTimestamp(timestamp: Long): List<HeartRateData>



    @Query("SELECT * FROM heartrate_table WHERE timestamp >= :todayStart ORDER BY timestamp DESC LIMIT 1")
    fun fetchLatestHeartRateData(todayStart: Long): LiveData<HeartRateData?>


    @Update
    suspend fun update(heartRateData: HeartRateData)


    @Query("UPDATE heartrate_table SET value = :value WHERE timestamp = :timestamp")
    suspend fun updateValue(timestamp: Long, value: Int)



    @Query("SELECT COUNT(*) FROM heartrate_table WHERE timestamp BETWEEN :startTime AND :endTime AND isMeasurement = 1")
    suspend fun countActiveHeartRateTests(startTime: Long, endTime: Long): Int




}
