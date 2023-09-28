package com.ivy.common.androidtest

import com.ivy.common.time.provider.TimeProvider
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class TimeProviderFake: TimeProvider{
    private val localDateTime = LocalDateTime.now()
    private val dateNow = LocalDate.now()
    private val zoneId = ZoneId.systemDefault()


    override fun timeNow(): LocalDateTime = localDateTime

    override fun dateNow(): LocalDate = dateNow

    override fun zoneId(): ZoneId = zoneId

}