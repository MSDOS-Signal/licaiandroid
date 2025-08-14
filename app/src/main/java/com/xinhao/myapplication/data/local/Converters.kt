package com.xinhao.myapplication.data.local

import androidx.room.TypeConverter
import com.xinhao.myapplication.data.model.TransactionType
import java.time.LocalDateTime
import java.time.YearMonth

class Converters {
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun fromTransactionType(value: TransactionType): String {
        return value.name
    }

    @TypeConverter
    fun toTransactionType(value: String): TransactionType {
        return TransactionType.valueOf(value)
    }

    @TypeConverter
    fun fromYearMonth(value: String?): YearMonth? {
        return value?.let { YearMonth.parse(it) }
    }

    @TypeConverter
    fun yearMonthToString(yearMonth: YearMonth?): String? {
        return yearMonth?.toString()
    }
}
