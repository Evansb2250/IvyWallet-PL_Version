package com.ivy.common.androidtest

import com.ivy.common.time.provider.TimeProvider
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class TimeProviderFake : TimeProvider {
    var timeNow = LocalDateTime.now()
    var dateNow = LocalDate.now()
    private val zoneId = ZoneId.systemDefault()


    override fun timeNow(): LocalDateTime = timeNow

    override fun dateNow(): LocalDate = dateNow

    override fun zoneId(): ZoneId = zoneId

}