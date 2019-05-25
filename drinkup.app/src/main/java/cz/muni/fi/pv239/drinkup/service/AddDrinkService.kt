package cz.muni.fi.pv239.drinkup.service

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.room.RxRoom
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import cz.muni.fi.pv239.drinkup.database.AppDatabase
import cz.muni.fi.pv239.drinkup.database.entity.Drink
import cz.muni.fi.pv239.drinkup.database.entity.DrinkingSession
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import khronos.toString
import java.util.*

class AddDrinkService{
    companion object{
        private var lon = 0.0
        private var lat = 0.0
        @JvmStatic
        fun addDrink(context: Context, drink: Drink): Flowable<Unit>{
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val db = AppDatabase.getAppDatabase(context)
            AchievementService.achievements(drink.category, context)
            val client = LocationServices.getFusedLocationProviderClient(context)
            if (ContextCompat.checkSelfPermission(
                    context.applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                client.lastLocation.addOnSuccessListener { location : Location? ->
                    saveLocation(location)
                    Toast.makeText(context, location.toString(), Toast.LENGTH_LONG).show()
                }
            }
            if (sharedPreferences.getBoolean("is_active_session", false)){
                return RxRoom.createFlowable(db)
                        .observeOn(Schedulers.io())
                        .map{db?.sessionDao()?.getLastSession() ?: error("canot get last session")}
                        .map { db?.drinkDao()?.insertDrink(copyDrink(drink, it.id, lon, lat))
                                ?: error("cannot add drink to db")}
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
            }
        }

        @JvmStatic
        private fun copyDrink(drink: Drink, sessionId: Long?, lon: Double, lat: Double): Drink{
            return Drink(sessionId = sessionId,
                    name = drink.name, price = drink.price, volume = drink.volume,
                    abv = drink.abv, category = drink.category,
                    longitude = lon, latitude = lat)
        }
        private fun saveLocation(location: Location?) {
            lon = location?.longitude ?: 0.0
            lat = location?.latitude ?: 0.0
        }
    }
}