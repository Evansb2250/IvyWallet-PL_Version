package com.ivy.core.domain.action.transaction

import com.ivy.common.androidtest.TimeProviderFake
import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.domain.action.AccountCacheFakeDao
import com.ivy.core.domain.action.TransactionDaoFake
import com.ivy.core.domain.algorithm.accountcache.InvalidateAccCacheAct
import com.ivy.core.persistence.algorithm.accountcache.AccountCacheDao
import com.ivy.core.persistence.dao.trn.TransactionDao
import org.junit.jupiter.api.BeforeEach

internal class WriteTrnsActTest {
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
}