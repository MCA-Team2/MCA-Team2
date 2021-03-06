/*
 * Chobo Counselor
 * Copyright (C) 2021 BLUESIGNUM Corp.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jwpyo.datalayerpractice.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.jwpyo.datalayerpractice.model.voice.Voice
import kotlinx.coroutines.flow.Flow

@Dao
interface VoiceDao {
    @Insert
    fun insert(voice: Voice): Long

    @Delete
    fun delete(voice: Voice)

    @Query("SELECT * FROM Voice")
    fun getVoices(): Flow<List<Voice>>
}
