package com.ivy.core.domain.algorithm.calc

import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.isEqualTo
import com.ivy.core.domain.utility.Months
import com.ivy.core.domain.utility.TransactionTime
import com.ivy.core.domain.utility.buildTransactionList
import com.ivy.core.domain.utility.transactionTimeBuilder
import com.ivy.core.persistence.algorithm.calc.CalcTrn
import com.ivy.data.CurrencyCode
import com.ivy.data.transaction.TransactionType
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.time.Instant
import java.util.stream.Stream

class RawStatsKtTest {


    @Test
    fun `RawStats containts zero Income from empty list`() {
        val result = rawStats(emptyList())
        val expectedIncomeCount = 0
        assertThat(expectedIncomeCount).isEqualTo(result.incomesCount)
    }

    @Test
    fun `Rawstats contains Income of 30 cents`() {
        val result = rawStats(
            buildTransactionList(
                CalcTrn.createTransactionObject().copy(
                    amount = 0.30,
                    currency = usdCode
                ),
            )
        )

        val expectedCurrencyCode: CurrencyCode = usdCode
        val expectedIncomeCount = 1
        val expecedAmount = 0.30

        assertThat(result.incomes.keys).containsOnly(expectedCurrencyCode)
        assertThat(result.incomesCount).isEqualTo(expectedIncomeCount)
        assertThat(result.incomes[usdCode]).isEqualTo(expecedAmount)
    }


    @Test
    fun `RawStats accumulate income total by currency`() {
        val result = rawStats(
            buildTransactionList(
                CalcTrn.createTransactionObject().copy(
                    amount = 1.20,
                    currency = usdCode,
                ),
                CalcTrn.createTransactionObject().copy(
                    amount = 2.00,
                    currency = usdCode,
                ),
                CalcTrn.createTransactionObject().copy(
                    amount = 1.80,
                    currency = usdCode,
                ),
                CalcTrn.createTransactionObject().copy(
                    amount = 5.80,
                    currency = colCode,
                ),
                CalcTrn.createTransactionObject().copy(
                    amount = 9.00,
                    currency = euroCode
                )
            )
        )

        val expectedUsdIncomeTotal = 5.00
        val expectedForeignIncomeTotal = 14.80

        assertThat(result.incomes.filter { it.key == usdCode }.values.sum()).isEqualTo(
            expectedUsdIncomeTotal
        )
        assertThat(result.incomes.filter { it.key != usdCode }.values.sum()).isEqualTo(
            expectedForeignIncomeTotal
        )
    }

    @ParameterizedTest
    @MethodSource("getRawStatUseCases")
    fun `RawStats check function under differentStates`(useCase: RawStateUseCase) {
        val result = rawStats(
            useCase.transactions
        )

        assertThat(result.incomesCount).isEqualTo(useCase.expectationIncomeCount)
        assertThat(result.expensesCount).isEqualTo(useCase.expectedExpenseCount)

        assertThat(result.incomes.values.sum()).isEqualTo(useCase.expectedIncomeTotal)
        assertThat(result.expenses.values.sum()).isEqualTo(useCase.expectedExpenseTotal)

        assertThat(result.incomes.keys.map { it }
            .toSet()).isEqualTo(useCase.expectedIncomeCurrencies)
        assertThat(result.expenses.keys.map { it }
            .toSet()).isEqualTo(useCase.expectedExpenseCurrencies)
        assertThat(result.newestTrnTime.epochSecond).isEqualTo(useCase.expectedLatestTrnTime.epochSecond)
    }


    data class RawStateUseCase(
        val transactions: List<CalcTrn> = emptyList(),
        val expectationIncomeCount: Int = 0,
        val expectedExpenseCount: Int = 0,
        val expectedIncomeTotal: Double = 0.00,
        val expectedExpenseTotal: Double = 0.00,
        val expectedIncomeCurrencies: Set<CurrencyCode> = emptySet(),
        val expectedExpenseCurrencies: Set<CurrencyCode> = emptySet(),
        val expectedLatestTrnTime: Instant,
    )

    companion object {
        private const val usdCode = "USD"
        private const val colCode = "COL"
        private const val euroCode = "EURO"

        @JvmStatic
        fun getRawStatUseCases(): Stream<RawStateUseCase> =
            Stream.of(
                //Testing to see if I get back both income and expenses
                RawStateUseCase(
                    transactions = buildTransactionList(
                        CalcTrn.createTransactionObject(
                            amount = 4.00,
                            type = TransactionType.Income,
                            time = transactionTimeBuilder(
                                Months.FEBRUARY,
                                25,
                                2023,
                                TransactionTime.getHour(9),
                                TransactionTime.getMinute(30),
                            )
                        ),
                        CalcTrn.createTransactionObject(
                            amount = 2.00,
                            type = TransactionType.Income,
                            time = transactionTimeBuilder(
                                Months.MAY,
                                25,
                                2023,
                                TransactionTime.getHour(9),
                                TransactionTime.getMinute(30),
                            )
                        ),
                        CalcTrn.createTransactionObject(
                            amount = 30.00,
                            type = TransactionType.Expense,
                            time = transactionTimeBuilder(
                                Months.JUNE,
                                25,
                                2023,
                                TransactionTime.getHour(9),
                                TransactionTime.getMinute(30),
                            )
                        )
                    ),
                    expectationIncomeCount = 2,
                    expectedExpenseCount = 1,
                    expectedExpenseTotal = 30.00,
                    expectedIncomeTotal = 6.00,
                    expectedIncomeCurrencies = setOf(
                        usdCode,
                    ),
                    expectedExpenseCurrencies = setOf(
                        usdCode
                    ),
                    expectedLatestTrnTime = transactionTimeBuilder(
                        Months.JUNE,
                        25,
                        2023,
                        TransactionTime.getHour(9),
                        TransactionTime.getMinute(30),
                    )
                ),
                //Expenses from two different currencies
                RawStateUseCase(
                    transactions = buildTransactionList(
                        CalcTrn.createTransactionObject(
                            amount = 40.00,
                            currency = euroCode,
                            type = TransactionType.Expense,
                            time = transactionTimeBuilder(
                                Months.JUNE,
                                25,
                                2023,
                                TransactionTime.getHour(9),
                                TransactionTime.getMinute(30),
                            )
                        ),
                        CalcTrn.createTransactionObject(
                            amount = 10.00,
                            currency = colCode,
                            type = TransactionType.Expense,
                            time = transactionTimeBuilder(
                                Months.SEPTEMBER,
                                25,
                                2023,
                                TransactionTime.getHour(9),
                                TransactionTime.getMinute(30),
                            )
                        ),
                        CalcTrn.createTransactionObject(
                            amount = 15.00,
                            currency = colCode,
                            type = TransactionType.Expense,
                            time = transactionTimeBuilder(
                                Months.SEPTEMBER,
                                25,
                                2023,
                                TransactionTime.getHour(9),
                                TransactionTime.getMinute(35),
                            )
                        )
                    ),
                    expectedExpenseTotal = 65.00,
                    expectedExpenseCount = 3,
                    expectedExpenseCurrencies = setOf(
                        euroCode,
                        colCode
                    ),
                    expectedLatestTrnTime = transactionTimeBuilder(
                        Months.SEPTEMBER,
                        25,
                        2023,
                        TransactionTime.getHour(9),
                        TransactionTime.getMinute(35),
                    )
                )
            )

    }


}