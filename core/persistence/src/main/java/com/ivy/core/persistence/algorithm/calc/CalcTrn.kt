package com.ivy.core.persistence.algorithm.calc

import androidx.room.ColumnInfo
import com.ivy.data.CurrencyCode
import com.ivy.data.transaction.TransactionType
import java.time.Instant

data class CalcTrn(
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name = "currency")
    val currency: CurrencyCode,
    @ColumnInfo(name = "type")
    val type: TransactionType,
    @ColumnInfo(name = "time")
    val time: Instant,
){
  companion object {
      fun createTransactionObject(
          amount: Double = 2.00,
          currency: String = "USD",
          type: TransactionType=  TransactionType.Income,
          time: Instant?= null
      ): CalcTrn = CalcTrn(
          amount = amount,
          currency = currency,
          type =  type,
          time = time ?: Instant.now(),
      )
  }
}
