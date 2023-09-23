package com.ivy.core.domain.action.exchange

import assertk.assertThat
import assertk.assertions.isGreaterThan
import assertk.assertions.isNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SyncExchangeRatesActTest {
    private lateinit var synchExchangeRatesAct: SyncExchangeRatesAct
    private lateinit var exchangeProviderFake: RemoteExchangeProviderFake
    private lateinit var exchangeRateDaoFake: ExchangeRateDaoFake

    @BeforeEach
    fun setUp() {
        exchangeProviderFake = RemoteExchangeProviderFake()
        exchangeRateDaoFake = ExchangeRateDaoFake()
        synchExchangeRatesAct = SyncExchangeRatesAct(
            exchangeProvider = exchangeProviderFake,
            exchangeRateDao = exchangeRateDaoFake
        )
    }

    @Test
    fun `Test sync exchange rates negative values ignored`() = runBlocking {
        synchExchangeRatesAct("USD")

        val usdRates = exchangeRateDaoFake
            .findAllByBaseCurrency("USD")
            .first { it.isNotEmpty() }
        val cadRate = usdRates.find { it.currency == "CAD" }

        assertThat(cadRate).isNull()
    }

    @Test
    fun `Test sync exchange rates, valid values saved`() = runBlocking {
        synchExchangeRatesAct("USD")

       val result = exchangeRateDaoFake.findAllByBaseCurrency("USD")
           .first {
               it.isNotEmpty()
           }

        result.forEach {
            assertThat(it.rate).isGreaterThan(0.00)
        }

    }
}