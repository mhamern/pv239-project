package cz.muni.fi.pv239.drinkup.formatter.statistics

import com.github.mikephil.charting.formatter.ValueFormatter

class NoValueChartFormatter() : ValueFormatter() {


    override fun getFormattedValue(value: Float): String {
        return ""
    }
}