package com.jwpyo.soundmind.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bluesignum.counselor.persistence.converter.LocalDateConverter
import com.bluesignum.counselor.persistence.converter.LocalDateTimeConverter
import com.bluesignum.counselor.persistence.converter.LocalTimeConverter
import com.jwpyo.soundmind.model.ppg.PPG
import com.jwpyo.soundmind.model.voice.Voice
import com.jwpyo.soundmind.persistence.dao.PPGDao
import com.jwpyo.soundmind.persistence.dao.VoiceDao

@Database(
    entities = [
        Voice::class,
        PPG::class,
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
    abstract fun getPPGDao(): PPGDao
}