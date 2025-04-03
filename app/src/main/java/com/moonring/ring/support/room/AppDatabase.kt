package com.moonring.ring.support.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.moonring.ring.bean.room.CalData
import com.moonring.ring.bean.room.HeartRateData
import com.moonring.ring.bean.room.SleepData
import com.moonring.ring.bean.room.StepData
import com.moonring.ring.bean.room.OxygenData
import com.moonring.ring.support.room.dao.CalDataDao
import com.moonring.ring.support.room.dao.HeartRateDao
import com.moonring.ring.support.room.dao.OxygenDao
import com.moonring.ring.support.room.dao.SleepDao
import com.moonring.ring.support.room.dao.StepDao
import net.sqlcipher.database.SupportFactory
import androidx.sqlite.db.SupportSQLiteDatabase
import com.module.common.support.config.AppConfig
import net.sqlcipher.database.SQLiteDatabase


@Database(entities = [StepData::class, SleepData::class, HeartRateData::class, OxygenData::class, CalData::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stepDao(): StepDao
    abstract fun sleepDao(): SleepDao
    abstract fun heartRateDao(): HeartRateDao
    abstract fun oxygenDao(): OxygenDao
    abstract fun calDataDao(): CalDataDao

    companion object {
        @Volatile
        private var INSTANCE: HashMap<String, AppDatabase> = HashMap()

        fun getDatabase(context: Context, userId: String): AppDatabase {

            val shouldEncrypt = AppConfig.enviroment == AppConfig.Enviroment.prod|| AppConfig.enviroment == AppConfig.Enviroment.proda
            val builder = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "health_data_database_$userId"
            )

            if (shouldEncrypt) {
                val passphrase: ByteArray = SQLiteDatabase.getBytes(sqlps.toCharArray())
                val factory = SupportFactory(passphrase)
                builder.openHelperFactory(factory)
            }

            return INSTANCE[userId] ?: synchronized(this) {
                val instance = builder
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE[userId] = instance
                instance
            }
        }
    }
}
