package com.jwpyo.soundmind.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jwpyo.soundmind.model.stress.Stress
import com.jwpyo.soundmind.model.voice.Voice
import com.jwpyo.soundmind.persistence.converter.LocalDateConverter
import com.jwpyo.soundmind.persistence.converter.LocalDateTimeConverter
import com.jwpyo.soundmind.persistence.converter.LocalTimeConverter
import com.jwpyo.soundmind.persistence.dao.StressDao
import com.jwpyo.soundmind.persistence.dao.VoiceDao

@Database(
    entities = [
        Voice::class,
        Stress::class,
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(
    LocalDateTimeConverter::class,
    LocalDateConverter::class,
    LocalTimeConverter::class,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getVoiceDao(): VoiceDao
    abstract fun getStressDao(): StressDao
}