package com.jwpyo.soundmind.persistence

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Voice ADD COLUMN `text` TEXT NOT NULL DEFAULT ''")
        database.execSQL("ALTER TABLE Voice ADD COLUMN `hasBell` INTEGER NOT NULL DEFAULT 0")
    }
}

val ALL_MIGRATIONS = arrayOf(
    MIGRATION_1_2,
)