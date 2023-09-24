package com.ivy.core.domain.utility

import com.ivy.core.persistence.algorithm.calc.CalcTrn
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeParseException

fun buildTransactionList(vararg transaction: CalcTrn): List<CalcTrn> {
    return transaction.toList()
}


fun transactionTimeBuilder(
    month: Months,
    day: Int,
    year: Int,
    hour: TransactionTime.Hour,
    minute: TransactionTime.Minute
): Instant {

    //I can define a valid range
    val validMonthRange = 1..12
    val validYearRange = 2000..2023
    val validDayRange = setValidDayRange(
        month = month,
        year = year
    )

    require(day in validDayRange) { "Invalid day for this month" }
    require(year in validYearRange) { "Invalid year range" }

    return try {
        val localDate = LocalDate.of(year, month.value, day)
        val formattedDate = localDate.toString()
        Instant.parse(
            "$formattedDate T${hour.toString().padStart(2, '0')}:${
                minute.toString().padStart(2, '0')
            }:00Z"
        )
    } catch (e: DateTimeParseException) {
        return Instant.now()
    }
}



object TransactionTime{
    fun getHour(hour: Int)
            : Hour = Hour(hour)

    fun getMinute(minute: Int): Minute = Minute(minute)
    data class Hour(val hour: Int) {
        init {
            require(hour in 0..23) { "Hour must be between 0 and 23" }
        }
    }

    data class Minute(val minutes: Int) {
        init {
            require(minutes in 0..59) { "Minutes must be between 0 and 59" }
        }
    }
}



private fun setValidDayRange(
    month: Months,
    year: Int,
): IntRange = when (
    month
) {
    Months.JANUARY, Months.MARCH, Months.MAY, Months.JULY, Months.AUGUST, Months.OCTOBER, Months.DECEMBER -> {
        1..31
    }

    Months.APRIL, Months.JUNE, Months.SEPTEMBER, Months.NOVEMBER -> {
        1..30
    }

    Months.FEBRUARY -> {
        if (year % 4 == 0) {
            1..29
        } else
            1..28
    }
}

enum class Months(val value: Int) {
    JANUARY(value = 1),
    FEBRUARY(value = 2),
    MARCH(value = 3),
    APRIL(value = 4),
    MAY(value = 5),
    JUNE(value = 6),
    JULY(value = 7),
    AUGUST(value = 8),
    SEPTEMBER(value = 9),
    OCTOBER(value = 10),
    NOVEMBER(value = 11),
    DECEMBER(value = 12),
}