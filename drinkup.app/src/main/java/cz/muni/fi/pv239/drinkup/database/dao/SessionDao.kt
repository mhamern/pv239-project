package cz.muni.fi.pv239.drinkup.database.dao

import androidx.room.*
import cz.muni.fi.pv239.drinkup.database.entity.Drink
import cz.muni.fi.pv239.drinkup.database.entity.DrinkingSession

@Dao
interface SessionDao {
    @Insert
    fun insertSession(drinkingSession: DrinkingSession)

    @Update
    fun updateSession(drinkingSession: DrinkingSession)

    @Delete
    fun deleteSession(drinkingSession: DrinkingSession)

    @Query("SELECT * FROM Sessions")
    fun getAllSessions(): List<DrinkingSession>

    @Query("SELECT * FROM Drinks WHERE sessionId = :sessionId")
    fun getAllDrinks(sessionId: Long): List<Drink>
}