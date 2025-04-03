package com.moonring.ring.bean.room

import androidx.room.Entity
import com.moonring.ring.enums.HealthDataType
import com.moonring.ring.utils.timestampToDateHH
import com.moonring.ring.utils.timestampToDateYMD
import com.moonring.ring.utils.timestampToDateYMDHMSS

/*

yyyy-MM-dd HH:mm:ss.SSS Z
HH
yyyy-MM-dd
 */
@Entity(tableName = "cal_table")
class CalData(
    timestamp: Long,
    value: Int,
    date: String  = timestampToDateYMDHMSS(timestamp),
    dateHour: String = timestampToDateHH(timestamp),
    dateYMD: String = timestampToDateYMD(timestamp),
    isUpload: Boolean  = false,
): RingBaseDataModel(timestamp, value, date, dateHour, dateYMD, isUpload) {
    override fun uploadData(): Map<String, Any> {
        return mapOf(
            "metric" to HealthDataType.calorie.name,
            "value" to value,
            "timestamp" to timestamp
        )
    }

}