package cz.muni.fi.pv239.drinkup.enum
import android.content.Context
import android.provider.MediaStore.Audio.AlbumColumns.LAST_YEAR
import cz.muni.fi.pv239.drinkup.R
import khronos.*
import khronos.Dates.today
import java.util.*

enum class StatisticsTimePeriod {
    LAST_WEEK {
        override fun toStringLocalized(context: Context?): String {
            return context?.getString(R.string.statistics_time_period_last_week) ?: "Last week"
        }
    },
    LAST_MONTH {
        override fun toStringLocalized(context: Context?): String {
            return context?.getString(R.string.statistics_time_period_last_month) ?: "Last month"
        }
    },
    LAST_YEAR {
        override fun toStringLocalized(context: Context?): String {
            return context?.getString(R.string.statistics_time_period_last_year) ?: "Last year "
        }
    };

    abstract fun toStringLocalized(context: Context?): String

    companion object {
        fun getFromDate(timePeriod: StatisticsTimePeriod): Date {
            return when (timePeriod) {
                LAST_WEEK -> today - 1.week
                LAST_MONTH -> today - 2.month // Inconsistency in library. Month is 1-based, while others are 0-based. 👌
                LAST_YEAR -> today - 1.year
            }
        }


        fun transformDateToBeginningOfTimePeriod(timePeriod: StatisticsTimePeriod, date: Date): Date {
            return when(timePeriod) {
                StatisticsTimePeriod.LAST_WEEK -> date.beginningOfDay
                StatisticsTimePeriod.LAST_MONTH -> date.beginningOfDay
                StatisticsTimePeriod.LAST_YEAR -> date.beginningOfMonth
            }
        }

        fun createDatesForTimePeriod(timePeriod: StatisticsTimePeriod): List<Date> {
            return when (timePeriod) {
                StatisticsTimePeriod.LAST_WEEK -> createDaysForLastWeek()
                StatisticsTimePeriod.LAST_MONTH -> createDaysForLastMonth()
                StatisticsTimePeriod.LAST_YEAR -> createMonthsForLastYear()
            }
        }

        private fun createMonthsForLastYear(): List<Date> {
            val months = ArrayList<Date>()
            var from = getFromDate(LAST_YEAR).beginningOfMonth
            val to = Dates.today.beginningOfMonth

            while (from < to) {
                months.add(from)
                from = (from + 2.month).beginningOfMonth
            }
            return months
        }

        private fun createDaysForLastMonth(): List<Date> {
            val days = ArrayList<Date>()
            var from = getFromDate(LAST_MONTH).beginningOfDay
            val to = Dates.today.beginningOfDay

            while (from < to) {
                days.add(from)
                from = (from + 1.day).beginningOfDay
            }
            return days
        }

        private fun createDaysForLastWeek(): List<Date> {
            val days = ArrayList<Date>()
            var from = getFromDate(LAST_WEEK).beginningOfDay
            val to = Dates.today.beginningOfDay

            while (from < to) {
                days.add(from)
                from = (from + 1.day).beginningOfDay
            }
            return days
        }
    }
}