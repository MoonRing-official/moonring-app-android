package com.moonring.ring.bean.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.moonring.ring.support.interfaces.IUploadData
import com.moonring.ring.utils.timestampToDateHH
import com.moonring.ring.utils.timestampToDateYMD
import com.moonring.ring.utils.timestampToDateYMDHMSS

@Entity(tableName = "ring_data_table")
open abstract class RingBaseDataModel(
    @PrimaryKey
    @ColumnInfo(name = "timestamp") open var timestamp: Long = 0,
    @ColumnInfo(name = "value") open var value: Int = 0,
    @ColumnInfo(name = "date") open var date: String = "",
    @ColumnInfo(name = "dateHour") open var dateHour: String = "",
    @ColumnInfo(name = "dateYMD") open var dateYMD: String = "",
    @ColumnInfo(name = "isUpload") open var isUpload: Boolean = false
): IUploadData
