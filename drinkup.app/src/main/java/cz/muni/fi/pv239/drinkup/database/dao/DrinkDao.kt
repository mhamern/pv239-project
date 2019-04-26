package cz.muni.fi.pv239.drinkup.database.dao

import androidx.room.*
import cz.muni.fi.pv239.drinkup.database.entity.Drink

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
}