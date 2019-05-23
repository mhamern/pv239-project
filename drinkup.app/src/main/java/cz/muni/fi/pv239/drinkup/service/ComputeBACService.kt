package cz.muni.fi.pv239.drinkup.service

import android.content.Context
import android.preference.PreferenceManager
import cz.muni.fi.pv239.drinkup.R
import cz.muni.fi.pv239.drinkup.database.entity.Drink

class ComputeBACService {
    companion object {
        @JvmStatic
        fun computeBAC(context: Context, drinks: List<Drink>): Double? {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val weightSP = sharedPreferences.getString("pref_weight", "")
            val genderSP = sharedPreferences.getString("pref_gender", "")
            val weight: Double
            val gender: Int
            if (weightSP == "" || genderSP == "") {
                return null
            } else {
                weight = weightSP.toDouble()
                gender = genderSP.toInt()
            }
            val genderConst: Double
            if (gender == 0) {
                genderConst = 0.68
            } else {
                genderConst = 0.55
            }
            val sortedList = drinks.sortedWith(compareBy { it.date })
            val time: Double = (sortedList.last().date.time - sortedList.first().date.time).toDouble()/1000/60/60
            var goa = 0.0
            for(drink in drinks) {
                goa += (drink.volume * (drink.abv / 100) * 0.789)
            }
            return ((goa/(weight*1000*genderConst))*100 - time*0.015)*10
        }
    }
}