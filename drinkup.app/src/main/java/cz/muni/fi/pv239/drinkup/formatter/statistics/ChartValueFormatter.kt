package cz.muni.fi.pv239.drinkup.formatter.statistics

import com.github.mikephil.charting.formatter.ValueFormatter


class ChartValueFormatter(private val suffix: String) : ValueFormatter() {


    override fun getFormattedValue(value: Float): String {
       return "$value $suffix"
    }
}