package com.jwpyo.datalayerpractice.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bluesignum.counselor.persistence.converter.LocalDateConverter
import com.bluesignum.counselor.persistence.converter.LocalDateTimeConverter
import com.bluesignum.counselor.persistence.converter.LocalTimeConverter
import com.jwpyo.datalayerpractice.model.voice.Voice
import com.jwpyo.datalayerpractice.persistence.dao.VoiceDao

@Database(
    entities = [
        Voice::class,
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(
    LocalDateTimeConverter::class,
    LocalDateConverter::class,
    LocalTimeConverter::class,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getVoiceDao(): VoiceDao
}