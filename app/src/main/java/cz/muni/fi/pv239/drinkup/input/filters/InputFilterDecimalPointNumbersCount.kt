package cz.muni.fi.pv239.drinkup.input.filters

import android.text.InputFilter
import android.text.Spanned

class InputFilterDecimalPointNumbersCount(private val beforeDecimal: Int, private val afterDecimal: Int): InputFilter {

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int): CharSequence {
        val builder = StringBuilder(dest)
        builder.insert(dstart, source)
        val temp = builder.toString()

        if (temp == ".") {
            return "0."
        } else if (temp.indexOf('.') == -1) {
            if (temp.length > beforeDecimal) {
                return ""
            }
        } else {
            if (temp.substring(0, temp.indexOf('.')).length > beforeDecimal || temp.substring(
                    temp.indexOf('.') + 1,
                    temp.length
                ).length > afterDecimal
            ) {
                return ""
            }
        }
        return source
    }
}