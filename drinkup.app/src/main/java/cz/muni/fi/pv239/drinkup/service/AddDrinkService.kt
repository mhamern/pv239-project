package cz.muni.fi.pv239.drinkup.service

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Transformations.map
import androidx.room.RxRoom
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.OnMapReadyCallback
import com.patloew.rxlocation.RxLocation
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
            AchievementService.achievements(drink.category, context)
            if (sharedPreferences.getBoolean("is_active_session", false)){
                return RxRoom.createFlowable(db)
                        .observeOn(Schedulers.io())
                    .map{db?.sessionDao()?.getLastSession() ?: error("cannot get last session")}
                        .map { db?.drinkDao()?.insertDrink(copyDrink(drink, it.id))
                                ?: error("cannot add drink to db")}
                    .map { if (lat == 0.0 || lon == 0.0) AddDrinkOperationResult.SUCCEEDED_WITHOUT_PERMISSION else AddDrinkOperationResult.SUCCEEDED }
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
                        .map { db?.drinkDao()?.insertDrink(copyDrink(drink, it.id))
                                ?: error("cannot add drink to db")}
                        .map { if (lat == 0.0 || lon == 0.0) AddDrinkOperationResult.SUCCEEDED_WITHOUT_PERMISSION else AddDrinkOperationResult.SUCCEEDED }
                        .onErrorReturn { AddDrinkOperationResult.FAILED }
            }
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