package com.moonring.ring.support.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.moonring.ring.bean.room.OxygenData
import com.moonring.ring.bean.room.SleepData
import com.moonring.ring.bean.room.StepData

@Dao
interface SleepDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(sleepData: SleepData): Long


    @Query("SELECT * FROM sleep_table WHERE timestamp BETWEEN :startTime AND :endTime")
    suspend fun fetchSleepDataByTimestamp(startTime: Long, endTime: Long): List<SleepData>

    @Query("SELECT * FROM sleep_table")
    fun fetchAllSleepData(): List<SleepData>

    @Query("SELECT * FROM sleep_table WHERE timestamp BETWEEN :startTime AND :endTime")
    fun fetchSleepDataByTimestampLive(startTime: Long, endTime: Long): LiveData<List<SleepData>>

    @Query("SELECT * FROM sleep_table WHERE timestamp < :timestamp")
    suspend fun fetchSleepDataBeforeTimestamp(timestamp: Long): List<SleepData>

    @Update
    suspend fun update(sleepData: SleepData)


    @Query("UPDATE sleep_table SET value = :value WHERE timestamp = :timestamp")
    suspend fun updateValue(timestamp: Long, value: Int)
}
