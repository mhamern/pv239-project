package cz.muni.fi.pv239.drinkup.fragment.statistics

import androidx.fragment.app.Fragment
import cz.muni.fi.pv239.drinkup.enum.StatisticsTimePeriod
import cz.muni.fi.pv239.drinkup.event.listener.OnStatisticsTImePeriodChangeListener

abstract class BaseChartFragment(private val initialTimePeriod: StatisticsTimePeriod): Fragment(), OnStatisticsTImePeriodChangeListener