package com.ivy.core.domain.utility

import com.ivy.core.persistence.algorithm.calc.CalcTrn
import com.ivy.data.CurrencyCode
import com.ivy.data.ItemIconId
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.Value
import com.ivy.data.account.Account
import com.ivy.data.account.AccountState
import com.ivy.data.attachment.Attachment
import com.ivy.data.category.Category
import com.ivy.data.category.CategoryState
import com.ivy.data.category.CategoryType
import com.ivy.data.tag.Tag
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnMetadata
import com.ivy.data.transaction.TrnPurpose
import com.ivy.data.transaction.TrnState
import com.ivy.data.transaction.TrnTime
import com.ivy.data.transaction.dummyTrnTimeActual
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import java.util.UUID

fun createSync(): Sync = Sync(
    state = SyncState.Synced,
    lastUpdated = LocalDateTime.now()
)
fun createTrnMetadata(): TrnMetadata = TrnMetadata(
    recurringRuleId = null,
    loanId = null,
    loanRecordId = null
)
fun createCategory(
    id: UUID
): Category = Category(
    id,
    name = "",
    type = CategoryType.Income,
    parentCategoryId = null,
    color = 0,
    icon = null,
    orderNum = 0.0,
    state = CategoryState.Archived,
    sync = Sync(
        state = SyncState.Synced,
        lastUpdated = LocalDateTime.now()
    )
)
fun createAccount(
    id: UUID,
    name: String = "",
    currency: CurrencyCode = CurrencyCode(),
    color: Int = 0,
    icon: ItemIconId? = null,
    excluded: Boolean = true,
    folderId: UUID? = null,
    orderNum: Double = 0.0,
    state: AccountState = AccountState.Default,
    sync: Sync = Sync(
        state = SyncState.Synced,
        lastUpdated = LocalDateTime.now(),
    )
): Account = Account(
    id = id,
    name = name,
    currency = currency,
    color = color,
    icon = icon,
    excluded = excluded,
    folderId = folderId,
    orderNum = orderNum,
    state = state,
    sync = sync
)
fun createTransaction(
    id: UUID = UUID.randomUUID(),
    account: Account = createAccount(id),
    type: TransactionType = TransactionType.fromCode(1) ?: TransactionType.Income,
    value: Value = Value(
        amount = 10.0,
        currency = "USD",
    ),
    category: Category? = createCategory(id),
    time: TrnTime = dummyTrnTimeActual(),
    title: String? = null,
    description: String? = null,
    state: TrnState = TrnState.Default,
    purpose: TrnPurpose? = TrnPurpose.fromCode(1) ?: TrnPurpose.Fee,
    tags: List<Tag> = emptyList(),
    attachments: List<Attachment> = emptyList(),
    metadata: TrnMetadata = createTrnMetadata(),
    sync: Sync = createSync(),
): Transaction = Transaction(
    id = id,
    account = account,
    type = type,
    value = value,
    category = category,
    time = time,
    title = title,
    description = description,
    state = state,
    purpose = purpose,
    tags = tags,
    attachments = attachments,
    metadata = metadata,
    sync = sync,
)
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
sealed class TransactionTime {

    companion object {
        fun getHour(hour: Int)
                : Hour = Hour(hour)

        fun getMinute(minute: Int): Minute = Minute(minute)
    }

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