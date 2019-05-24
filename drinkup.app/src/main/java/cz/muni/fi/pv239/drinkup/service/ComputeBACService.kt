package cz.muni.fi.pv239.drinkup.service

import android.content.Context
import android.preference.PreferenceManager
import cz.muni.fi.pv239.drinkup.R
import cz.muni.fi.pv239.drinkup.database.entity.Drink
import java.util.*

class ComputeBACService {
    companion object {
        @JvmStatic
        fun computeBAC(context: Context, drinks: List<Drink>, history: Boolean): Double? {
            if (drinks.isEmpty()) {
                return null
            }
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val weightSP = sharedPreferences.getString("pref_weight", "")
            val genderSP = sharedPreferences.getString("pref_gender", "")

            //set active true if there is active session and we compute bac for it otherwise false
            val active: Boolean
            active = if (history) {
                false
            } else {
                sharedPreferences.getBoolean("is_active_session", false)
            }

            val weight: Double
            val gender: Int
            if (weightSP == "" || genderSP == "") {
                return null
            } else {
                weight = weightSP.toDouble()
                gender = genderSP.toInt()
            }

            //set gender constant
            val genderConst: Double
            genderConst = if (gender == 0) {
                0.68
            } else {
                0.55
            }

            val sortedList = drinks.sortedWith(compareBy { it.date })

            //fill in list of bac for every drink
            var listOfBAC: MutableList<Double> = mutableListOf()
            var drinkBAC: Double
            for(drink in drinks) {
                var time: Double = if (active) {
                    (Date().time - drink.date.time).toDouble()
                } else {
                    (drink.date.time - sortedList.first().date.time).toDouble()
                }
                time = (time / 1000 / 60 / 60)*0.015
                drinkBAC = (((drink.volume * (drink.abv / 100) * 0.789)/(weight*1000*genderConst))*100 - time)*10
                if (drinkBAC > 0) {
                    listOfBAC.add(drinkBAC)
                } else
                    listOfBAC.add(0.0)
            }

            //sum bac of every drink
            var sum = 0.0
            for (i in listOfBAC) {
                sum += i
            }
            return sum
        }
    }
}