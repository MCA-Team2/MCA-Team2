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
package com.bluesignum.counselor.persistence.converter

import androidx.room.TypeConverter

abstract class BaseConverter<T> {

    /**
     * Converts the [value] to a String. The default implementation is to call [toString].
     *  This can be overridden.
     */
    @TypeConverter
    open fun toString(value: T?): String? = value?.toString()

    /**
     * Converts the [value] to a [T]. If the [value] is null or empty, the returned [T] is null.
     *  If not, this called [objectFromString]. This can be overridden.
     */
    @TypeConverter
    open fun fromString(value: String?): T? =
        if (value.isNullOrEmpty()) null else objectFromString(value)

    /**
     * Converts the [value] to a [T]
     */
    abstract fun objectFromString(value: String): T?
}
