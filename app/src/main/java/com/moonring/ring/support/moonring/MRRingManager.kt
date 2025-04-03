package com.moonring.ring.support.moonring

import com.google.gson.Gson
import com.moonring.ring.bean.SleepDurationModel
import com.moonring.ring.bean.room.SleepData
import com.moonring.ring.enums.SleepLevelType
import com.moonring.ring.utils.currentTimeInSec
import com.moonring.ring.utils.toTimeMills
import com.moonring.ring.utils.toTimeString

object MRRingManager {

    inline fun <reified T : Any> copyModel(m: T): T? {
        val json = try {
            Gson().toJson(m)
        } catch (e: Exception) {
            return null
        }
        return try {
            Gson().fromJson(json, T::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun defaultSleepData():SleepData{
        return SleepData(timestamp = 0,value = 0)
    }


    fun conversionSleepLineList(list: List<SleepData>): List<SleepData> {
        val timeList = mutableListOf<SleepData>()
        var prevRecord = list.firstOrNull() ?: defaultSleepData()
        val timeFormat = "yyyy-MM-dd HH:mm:ss.SSS Z"

        list.forEachIndexed { index, currRecord ->
            if (index == 0) {
                val firstModel = copyModel(currRecord) ?: defaultSleepData()
                firstModel.value = 0
                firstModel.timestamp = currRecord.timestamp
                firstModel.date = currRecord.date
                timeList.add(firstModel)
            }

            if (currRecord.timestamp - prevRecord.timestamp > 60) {
                val prevModel = copyModel(prevRecord) ?: defaultSleepData()
                prevModel.timestamp = prevRecord.timestamp + 60
                prevModel.date = prevModel.timestamp.toTimeMills().toTimeString(timeFormat)
                timeList.add(prevModel)

                val awakeModel = copyModel(currRecord) ?: defaultSleepData()
                awakeModel.date = currRecord.date
                awakeModel.timestamp = currRecord.timestamp
                awakeModel.value = 0
                timeList.add(awakeModel)
                prevRecord = currRecord
                return@forEachIndexed
            }

            if (currRecord.value != prevRecord.value) {
                val prevModel = copyModel(prevRecord) ?: defaultSleepData()
                prevModel.timestamp = prevRecord.timestamp + 60
                prevModel.date = prevModel.timestamp.toTimeMills().toTimeString(timeFormat)
                timeList.add(prevModel)
                prevRecord = currRecord
                return@forEachIndexed
            }

            prevRecord = currRecord

            if (index + 1 == list.size) {
                val recordModel = copyModel(currRecord) ?: defaultSleepData()
                recordModel.timestamp = currRecord.timestamp + 60
                recordModel.date = recordModel.timestamp.toTimeMills().toTimeString(timeFormat)
                timeList.add(recordModel)
            }
        }

        if (timeList.size <= 1) {
            timeList.clear()
        }

        return timeList
    }



    fun conversionSleepDurationList(list: List<SleepData>): List<SleepDurationModel> {
        val allList = mutableListOf<SleepDurationModel>()
        var prevRecord = list.firstOrNull() ?: defaultSleepData()

        list.forEachIndexed { index, currRecord ->
            if (index == 0) return@forEachIndexed

            if (currRecord.timestamp - prevRecord.timestamp > 0) {
                val model = SleepDurationModel()
                model.type = SleepLevelType.fromValue(currRecord.value)
                model.durationSecond = (currRecord.timestamp - prevRecord.timestamp).toInt()
                model.durationHour = model.durationSecond / 3600.0
                model.startTimeText = prevRecord.timestamp.toTimeMills().toTimeString("HH:mm")
                model.endTimestamp = currRecord.timestamp
                model.endTimeText = currRecord.timestamp.toTimeMills().toTimeString("HH:mm")
                allList.add(model)
                prevRecord = currRecord
            }
        }

        val durationList = mutableListOf<SleepDurationModel>()
        var prevDuration = allList.firstOrNull() ?: SleepDurationModel()
        allList.forEachIndexed { index, currDuration ->
            if (index == 0) {
                durationList.add(currDuration)
                return@forEachIndexed
            }

            if (prevDuration.type == currDuration.type) {
                val lastIndex = durationList.lastIndex
                val m = durationList[lastIndex]
                m.durationSecond += currDuration.durationSecond
                m.durationHour += currDuration.durationHour
                m.endTimestamp = currDuration.endTimestamp
                m.endTimeText = currDuration.endTimeText
                durationList[lastIndex] = m
            } else {
                durationList.add(currDuration)
                prevDuration = currDuration
            }
        }

        println(durationList)

        return durationList
    }
}
