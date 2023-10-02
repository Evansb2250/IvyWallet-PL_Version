package com.ivy.core.domain.action.transaction

import com.ivy.common.androidtest.TimeProviderFake
import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.domain.action.AccountCacheFakeDao
import com.ivy.core.domain.action.TransactionDaoFake
import com.ivy.core.domain.algorithm.accountcache.InvalidateAccCacheAct
import com.ivy.core.domain.utility.createAccount
import com.ivy.core.domain.utility.createTransaction
import com.ivy.core.persistence.algorithm.accountcache.AccountCacheDao
import com.ivy.core.persistence.dao.trn.TransactionDao
import com.ivy.data.Value
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class WriteTrnsActTest {
    private lateinit var writeTrnsAct: WriteTrnsAct
    private lateinit var transactionDao: TransactionDao
    private lateinit var timeProvider: TimeProvider
    private lateinit var invalidateAccChacheAct: InvalidateAccCacheAct
    private lateinit var accountCacheFakeDao: AccountCacheDao

    @BeforeEach
    fun setUp() {
        transactionDao = TransactionDaoFake()
        timeProvider = TimeProviderFake()
        accountCacheFakeDao = AccountCacheFakeDao()
        invalidateAccChacheAct = InvalidateAccCacheAct(
            accountCacheDao = accountCacheFakeDao,
            timeProvider = timeProvider
        )

        writeTrnsAct = WriteTrnsAct(
            transactionDao = transactionDao,
            trnsSignal = TrnsSignal(),
            timeProvider = timeProvider,
            invalidateAccCacheAct = invalidateAccChacheAct,
            accountCacheDao = accountCacheFakeDao
        )
    }

    @Test
    fun testObjectCreation() = runBlocking {
        val id: UUID = UUID.randomUUID()
        val title: String? = null
        val description: String? = null
        val transaction = createTransaction(
            id = id,
            account = createAccount(id),
            value = Value(10.0, "USD"),
            title = title,
            description = description,
        )

        writeTrnsAct.invoke(WriteTrnsAct.Input.CreateNew(transaction))

       val transactions =  transactionDao.findAllBlocking()

       assertEquals(10.0, transactions.first().amount)
    }

}