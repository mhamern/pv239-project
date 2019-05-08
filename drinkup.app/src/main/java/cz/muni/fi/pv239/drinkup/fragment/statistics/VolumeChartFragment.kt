package cz.muni.fi.pv239.drinkup.fragment.statistics

import androidx.fragment.app.Fragment
import cz.muni.fi.pv239.drinkup.enum.StatisticsTimePeriod
import cz.muni.fi.pv239.drinkup.event.listener.OnStatisticsTImePeriodChangeListener

class VolumeChartFragment(private val initialTimePeriod: StatisticsTimePeriod): BaseChartFragment(initialTimePeriod) {

    override fun onTimePeriodChanged(timePeriod: StatisticsTimePeriod) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}