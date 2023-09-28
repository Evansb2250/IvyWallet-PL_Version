package com.ivy.core.domain.action

import com.ivy.core.persistence.algorithm.accountcache.AccountCacheDao
import com.ivy.core.persistence.algorithm.accountcache.AccountCacheEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Instant

class AccountCacheFakeDao : AccountCacheDao {

    val accountCache = mutableMapOf<String, AccountCacheEntity>()
    override fun findAccountCache(accountId: String): Flow<AccountCacheEntity?> {
      return flow {
          accountCache[accountId]
      }
    }

    override suspend fun findTimestampById(accountId: String): Instant? {
        return accountCache[accountId]?.timestamp
    }

    override suspend fun save(cache: AccountCacheEntity) {
        accountCache[cache.accountId] = cache
    }

    override suspend fun delete(accountId: String) {
        accountCache.remove(accountId)
    }

    override suspend fun deleteAll() {
      accountCache.clear()
    }

}