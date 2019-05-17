package cz.muni.fi.pv239.drinkup.database.dao

import androidx.room.*
import cz.muni.fi.pv239.drinkup.database.entity.Drink
import java.util.*

@Dao
interface DrinkDao {
    @Insert
    fun insertDrink(drink: Drink)

    @Update
    fun updateDrink(drink: Drink)

    @Delete
    fun deleteDrink(drink: Drink)

    @Query("SELECT * FROM Drinks")
    fun getAllDrinks(): List<Drink>

    @Query("SELECT * FROM Drinks WHERE date > :from AND date < :to ")
    fun getDrinksFromToDate(from: Date, to: Date): List<Drink>


}