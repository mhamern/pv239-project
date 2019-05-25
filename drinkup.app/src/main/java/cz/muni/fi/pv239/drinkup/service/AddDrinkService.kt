package cz.muni.fi.pv239.drinkup.service

import android.content.Context
import android.preference.PreferenceManager
import androidx.room.RxRoom
import cz.muni.fi.pv239.drinkup.database.AppDatabase
import cz.muni.fi.pv239.drinkup.database.entity.Drink
import cz.muni.fi.pv239.drinkup.database.entity.DrinkingSession
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import khronos.toString
import java.util.*

class AddDrinkService{
    companion object {
        private var lat: Double = 0.0
        private var lon: Double = 0.0
        @JvmStatic
        fun addDrink(context: Context, drink: Drink): Flowable<AddDrinkOperationResult>{
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val db = AppDatabase.getAppDatabase(context)

            return RxRoom.createFlowable(db)
                    .observeOn(Schedulers.io())
                    .map{if (!(sharedPreferences.getBoolean("is_active_session", false)))
                    {db?.sessionDao()
                            ?.insertSession(DrinkingSession(title = Date().toString("dd-MMM-yyyy")))
                            ?: error("cannot insert session")}}
                .map{db?.sessionDao()?.getLastSession() ?: error("cannot get last session")}
                    .map { db?.drinkDao()?.insertDrink(copyDrink(drink, it.id))
                            ?: error("cannot add drink to db")}
                    .map{PreferenceManager
                            .getDefaultSharedPreferences(context)
                            .edit().putBoolean("is_active_session", true).apply()
                        AchievementService.achievements(drink.category, context)}
                    .map { if (lat == 0.0 || lon == 0.0) AddDrinkOperationResult.SUCCEEDED_WITHOUT_PERMISSION else AddDrinkOperationResult.SUCCEEDED }
                    .onErrorReturn { AddDrinkOperationResult.FAILED }
        }

        @JvmStatic
        private fun copyDrink(drink: Drink, sessionId: Long?): Drink {
            lat = drink.latitude
            lon = drink.longitude
            return Drink(sessionId = sessionId,
                    name = drink.name, price = drink.price, volume = drink.volume,
                    abv = drink.abv, category = drink.category,
                    longitude = drink.longitude, latitude = drink.latitude)
        }
    }
}