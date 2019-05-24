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
    companion object{
        @JvmStatic
        fun addDrink(context: Context, drink: Drink): Flowable<AddDrinkOperationResult>{
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val db = AppDatabase.getAppDatabase(context)
            //TODO test na permissions + ziskanie lat a lon
            val lon = null
            val lat = null
            if (sharedPreferences.getBoolean("is_active_session", false)){
                return RxRoom.createFlowable(db)
                        .observeOn(Schedulers.io())
                        .map{db?.sessionDao()?.getLastSession() ?: error("canot get last session")}
                        .map { db?.drinkDao()?.insertDrink(copyDrink(drink, it.id, lon, lat))
                                ?: error("cannot add drink to db")}
                    .map { if (lat == null || lon == null) AddDrinkOperationResult.SUCCEEDED_WITHOUT_PERMISSION else AddDrinkOperationResult.SUCCEEDED }
                    .onErrorReturn { AddDrinkOperationResult.FAILED }
            }
            else
            {
                return RxRoom.createFlowable(db)
                        .observeOn(Schedulers.io())
                        .map{db?.sessionDao()
                                ?.insertSession(DrinkingSession(title = Date().toString("dd-MMM-yyyy")))
                                ?: error("cannot insert session")}
                        .map{db?.sessionDao()?.getLastSession() ?: error("cannot get last session")}
                        .map { db?.drinkDao()?.insertDrink(copyDrink(drink, it.id, lon, lat))
                                ?: error("cannot add drink to db")}
                        .map { if (lat == null || lon == null) AddDrinkOperationResult.SUCCEEDED_WITHOUT_PERMISSION else AddDrinkOperationResult.SUCCEEDED }
                        .onErrorReturn { AddDrinkOperationResult.FAILED }
            }
        }

        @JvmStatic
        private fun copyDrink(drink: Drink, sessionId: Long?, lon: Double?, lat: Double?): Drink{
            return Drink(sessionId = sessionId,
                    name = drink.name, price = drink.price, volume = drink.volume,
                    abv = drink.abv, category = drink.category,
                    longitude = lon, latitude = lat)
        }
    }
}