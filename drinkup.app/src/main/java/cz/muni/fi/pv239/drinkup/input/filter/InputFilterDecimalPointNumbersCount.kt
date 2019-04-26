package cz.muni.fi.pv239.drinkup.input.filter

import android.content.Context
import android.text.InputFilter
import android.text.Spanned

class InputFilterDecimalPointNumbersCount(private val beforeDecimal: Int, private val afterDecimal: Int, private val context: Context): InputFilter {

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
        val decimalPointChar = context.getString(cz.muni.fi.pv239.drinkup.R.string.decimal_point_character)
        if (temp == decimalPointChar) {
            return "0$decimalPointChar"
        } else if (temp.indexOf(decimalPointChar) == -1) {
            if (temp.length > beforeDecimal) {
                return ""
            }
        } else {
            if (temp.substring(0, temp.indexOf(decimalPointChar)).length > beforeDecimal || temp.substring(
                    temp.indexOf(decimalPointChar) + 1,
                    temp.length
                ).length > afterDecimal
            ) {
                return ""
            }
        }
        return source
    }
}