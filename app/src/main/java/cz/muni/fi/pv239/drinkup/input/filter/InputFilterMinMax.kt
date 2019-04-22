package cz.muni.fi.pv239.drinkup.input.filter

import android.text.InputFilter
import android.text.Spanned

class InputFilterMinMax(private val min: Int, private val max: Int): InputFilter {

    override fun filter(
        source:CharSequence,
        start:Int, end:Int,
        dest: Spanned,
        dstart:Int,
        dend:Int): CharSequence {
        try
        {
            val input = (dest.subSequence(0, dstart).toString() + source + dest.subSequence(dend, dest.length)).toInt()
            if (!startsWithZero(source, dest) &&isInRange(min, max, input))
                return source
        }
        catch (nfe:NumberFormatException) {}
        return ""
    }

    private fun startsWithZero(currChar: CharSequence, dest: Spanned): Boolean {
        return dest.isEmpty() && currChar.startsWith('0')
    }

    private fun isInRange(a: Int, b: Int, c: Int):Boolean {
        return if (b > a) c in a..b else c in b..a
    }
}