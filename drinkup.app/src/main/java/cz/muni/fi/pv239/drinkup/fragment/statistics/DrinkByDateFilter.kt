package cz.muni.fi.pv239.drinkup.fragment.statistics

import cz.muni.fi.pv239.drinkup.database.entity.Drink
import java.util.*

class DrinkByDateFilter {

    companion object {
        @JvmStatic
        fun filter(drinks: List<Drink>, from: Date, to: Date): List<Drink> = drinks.filter {
            it.date.after(from) && it.date.before(to)
        }
    }

}