package com.ivy.data.account

import android.icu.util.ULocale
import androidx.annotation.ColorInt
import com.ivy.data.CurrencyCode
import com.ivy.data.ItemIconId
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.Value
import com.ivy.data.attachment.Attachment
import com.ivy.data.category.Category
import com.ivy.data.tag.Tag
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnMetadata
import com.ivy.data.transaction.TrnPurpose
import com.ivy.data.transaction.TrnState
import com.ivy.data.transaction.TrnTime
import com.ivy.data.transaction.dummyTrnTimeActual
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

@Deprecated("will be removed!")
data class Account(
    val id: UUID,
    val name: String,
    val currency: CurrencyCode,
    @ColorInt
    val color: Int,
    val icon: ItemIconId?,
    val excluded: Boolean,
    val folderId: UUID?,
    val orderNum: Double,
    val state: AccountState,
    val sync: Sync,
)



