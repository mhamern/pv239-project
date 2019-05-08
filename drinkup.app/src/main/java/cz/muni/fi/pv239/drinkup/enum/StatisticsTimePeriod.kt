package cz.muni.fi.pv239.drinkup.enum
import khronos.*
import khronos.Dates.today
import java.util.*

enum class StatisticsTimePeriod {
    LAST_WEEK,
    LAST_MONTH,
    LAST_YEAR;

    companion object {
        fun getFromDate(timePeriod: StatisticsTimePeriod): Date {
            return when (timePeriod) {
                LAST_WEEK -> today - 1.week
                LAST_MONTH -> today - 1.month
                LAST_YEAR -> today - 1.year
            }
        }
    }
}