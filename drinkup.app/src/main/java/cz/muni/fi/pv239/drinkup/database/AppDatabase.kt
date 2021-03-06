package cz.muni.fi.pv239.drinkup.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cz.muni.fi.pv239.drinkup.database.dao.DrinkDao
import cz.muni.fi.pv239.drinkup.database.dao.DrinkDefinitionDao
import cz.muni.fi.pv239.drinkup.database.dao.SessionDao
import cz.muni.fi.pv239.drinkup.database.entity.Drink
import cz.muni.fi.pv239.drinkup.database.entity.DrinkDefinition
import cz.muni.fi.pv239.drinkup.database.entity.DrinkingSession

@Database(entities = [Drink::class, DrinkingSession::class, DrinkDefinition::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun drinkDao(): DrinkDao
    abstract fun sessionDao(): SessionDao
    abstract fun drinkDefinitionDao(): DrinkDefinitionDao

    companion object {
        var INSTANCE: AppDatabase? = null

        fun getAppDatabase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "AppDB").build()
                }
            }
            return INSTANCE
        }

        fun destroyDatabase() {
            INSTANCE = null
        }
    }
}