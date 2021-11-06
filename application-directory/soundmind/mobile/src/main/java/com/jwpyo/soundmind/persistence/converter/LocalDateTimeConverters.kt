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
package com.jwpyo.soundmind.persistence.converter

import androidx.room.TypeConverter
import com.jwpyo.soundmind.extensions.toLocalDateTime
import com.jwpyo.soundmind.extensions.toTimestamp
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime

class LocalDateTimeConverter {
    @TypeConverter
    fun toLong(value: LocalDateTime): Long = value.toTimestamp()

    @TypeConverter
    fun fromLong(value: Long): LocalDateTime = value.toLocalDateTime()
}

class LocalDateConverter : BaseConverter<LocalDate>() {
    override fun objectFromString(value: String): LocalDate? = LocalDate.parse(value)
}

class LocalTimeConverter : BaseConverter<LocalTime>() {
    override fun objectFromString(value: String): LocalTime? = LocalTime.parse(value)
}
