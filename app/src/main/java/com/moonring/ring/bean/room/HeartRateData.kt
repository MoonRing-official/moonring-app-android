package com.moonring.ring.bean.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.moonring.ring.enums.HealthDataType
import com.moonring.ring.support.interfaces.IGetLasteTime
import com.moonring.ring.utils.timestampToDateHH
import com.moonring.ring.utils.timestampToDateYMD
import com.moonring.ring.utils.timestampToDateYMDHMSS
import com.moonring.ring.utils.toTimeMills
import com.moonring.ring.utils.toTimeString

@Entity(tableName = "heartrate_table")
class HeartRateData(
    timestamp: Long,
    value: Int,
    date: String  = timestampToDateYMDHMSS(timestamp),
    dateHour: String = timestampToDateHH(timestamp),
    dateYMD: String = timestampToDateYMD(timestamp),
    isUpload: Boolean  = false,
    @ColumnInfo(name = "isMeasurement") var isMeasurement: Boolean = false
): RingBaseDataModel(timestamp, value, date, dateHour, dateYMD, isUpload), IGetLasteTime {
    override fun uploadData(): Map<String, Any> {
        return mapOf(
            "metric" to HealthDataType.heart_rate.name,
            "value" to value,
            "timestamp" to timestamp
        )
    }
    override fun getTime():String {
        return timestamp.toTimeMills().toTimeString()
    }
}
