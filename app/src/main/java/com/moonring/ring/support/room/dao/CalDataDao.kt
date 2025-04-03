package com.moonring.ring.support.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.moonring.ring.bean.room.CalData

@Dao
interface CalDataDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(calData: CalData): Long


    @Query("SELECT * FROM cal_table WHERE timestamp BETWEEN :startTime AND :endTime")
    suspend fun fetchCalDataByTimestamp(startTime: Long, endTime: Long): List<CalData>

    @Query("SELECT * FROM cal_table WHERE timestamp < :timestamp")
    suspend fun fetchCalDataBeforeTimestamp(timestamp: Long): List<CalData>
    @Update
    suspend fun update(calData: CalData)






    @Query("SELECT SUM(value) FROM cal_table WHERE timestamp >= :todayStart AND timestamp <= :todayEnd")
    fun getTodayTotalCalories(todayStart: Long, todayEnd: Long): LiveData<Int>



    @Query("SELECT SUM(value) FROM cal_table WHERE timestamp >= :yesterdayStart AND timestamp <= :yesterdayEnd")
    fun getYesterdayTotalCalories(yesterdayStart: Long, yesterdayEnd: Long): LiveData<Int>



    @Query("SELECT timestamp FROM cal_table WHERE timestamp >= :todayStart AND timestamp <= :todayEnd ORDER BY timestamp DESC LIMIT 1")
    fun getTodayLatestTimestamp(todayStart: Long, todayEnd: Long): LiveData<Long?>



    @Query("UPDATE cal_table SET value = :value WHERE timestamp = :timestamp")
    suspend fun updateValue(timestamp: Long, value: Int)

}