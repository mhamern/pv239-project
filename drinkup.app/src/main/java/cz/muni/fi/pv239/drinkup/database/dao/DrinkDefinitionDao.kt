package cz.muni.fi.pv239.drinkup.database.dao

import androidx.room.*
import cz.muni.fi.pv239.drinkup.database.entity.DrinkDefinition

@Dao
interface DrinkDefinitionDao {
    @Insert
    fun insertDrinkDefiniton(drinkDef: DrinkDefinition)

    @Update
    fun updateDrinkDefinition(drinkDef: DrinkDefinition)

    @Delete
    fun deleteDrinkDefinition(drinkDef: DrinkDefinition)

    @Query("SELECT * FROM DrinkDefinitions")
    fun getAllDrinkDefinitions(): List<DrinkDefinition>
}