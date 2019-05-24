package cz.muni.fi.pv239.drinkup.formatter.statistics

import com.github.mikephil.charting.formatter.ValueFormatter
import cz.muni.fi.pv239.drinkup.enum.StatisticsTimePeriod
import java.text.SimpleDateFormat
import java.util.*


class TimePeriodAxisFormatter(
    initialTimePeriod: StatisticsTimePeriod): ValueFormatter() {

    var timePeriod: StatisticsTimePeriod = initialTimePeriod
        set(value) {
            field = value
            timePeriodDates = StatisticsTimePeriod.createDatesForTimePeriod(value)
            dateFormatter = updateFormatter(timePeriod)
        }

    private var timePeriodDates = StatisticsTimePeriod.createDatesForTimePeriod(timePeriod)
    private var dateFormatter = SimpleDateFormat("EE", Locale.getDefault())

    override fun getFormattedValue(value: Float): String {
        return if (value.toInt() < timePeriodDates.count()) {
            dateFormatter.format(timePeriodDates[value.toInt()])
        } else {
            ""
        }
    }

    private fun updateFormatter(timePeriod: StatisticsTimePeriod): SimpleDateFormat {
        return when (timePeriod) {
            StatisticsTimePeriod.LAST_YEAR -> SimpleDateFormat("MMMM", Locale.getDefault())
            StatisticsTimePeriod.LAST_MONTH -> SimpleDateFormat("d", Locale.getDefault())
            StatisticsTimePeriod.LAST_WEEK -> SimpleDateFormat("EE", Locale.getDefault())
        }
    }
}