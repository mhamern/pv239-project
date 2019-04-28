package cz.muni.fi.pv239.drinkup.database.dao

import androidx.room.*
import cz.muni.fi.pv239.drinkup.database.entity.Session

@Dao
interface SessionDao {
    @Insert
    fun insertSession(session: Session)

    @Update
    fun updateSession(session: Session)

    @Delete
    fun deleteSession(session: Session)

    @Query("SELECT * FROM Sessions")
    fun getAllSessions(): List<Session>

}