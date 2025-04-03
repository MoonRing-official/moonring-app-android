package com.moonring.ring.support.room.dao
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.moonring.ring.bean.room.CalData
import com.moonring.ring.bean.room.SleepData
import com.moonring.ring.bean.room.StepData

@Dao
interface StepDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(stepData: StepData): Long

    @Query("SELECT * FROM step_table WHERE timestamp BETWEEN :startTime AND :endTime")
    suspend fun fetchStepsByTimestamp(startTime: Long, endTime: Long): List<StepData>

    @Query("SELECT * FROM step_table WHERE timestamp < :timestamp")
    suspend fun fetchStepDataBeforeTimestamp(timestamp: Long): List<StepData>
    @Update
    suspend fun update(stepData: StepData)



    @Query("SELECT SUM(value) FROM step_table WHERE timestamp >= :todayStart")
    fun getTodayTotalSteps(todayStart: Long): LiveData<Int>



    @Query("SELECT timestamp FROM step_table WHERE timestamp >= :todayStart ORDER BY timestamp DESC LIMIT 1")
    fun getTodayLastStepTimestamp(todayStart: Long): LiveData<Long?>



    @Query("SELECT SUM(value) FROM step_table WHERE timestamp BETWEEN :yesterdayStart AND :yesterdayEnd")
    fun getYesterdayTotalSteps(yesterdayStart: Long, yesterdayEnd: Long): LiveData<Int>


    @Query("UPDATE step_table SET value = :value WHERE timestamp = :timestamp")
    suspend fun updateValue(timestamp: Long, value: Int)
}
