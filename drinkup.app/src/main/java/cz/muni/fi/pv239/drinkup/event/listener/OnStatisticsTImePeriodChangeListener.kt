package cz.muni.fi.pv239.drinkup.event.listener

import cz.muni.fi.pv239.drinkup.enum.StatisticsTimePeriod

interface OnStatisticsTImePeriodChangeListener {

    fun onTimePeriodChanged(timePeriod: StatisticsTimePeriod)
}