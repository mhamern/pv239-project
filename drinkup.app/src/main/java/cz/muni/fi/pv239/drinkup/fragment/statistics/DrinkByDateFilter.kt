package cz.muni.fi.pv239.drinkup.fragment.statistics

import cz.muni.fi.pv239.drinkup.database.entity.Drink
import java.util.*

class DrinkByDateFilter {

    fun filter(drinks: List<Drink>, from: Date, to: Date): List<Drink> = drinks.filter {
         it.location?.time ?: -1 > from.time && it.location?.time ?: -1 < to.time
     }
}