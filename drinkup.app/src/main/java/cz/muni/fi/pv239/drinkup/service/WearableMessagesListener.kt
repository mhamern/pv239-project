package cz.muni.fi.pv239.drinkup.service

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.room.RxRoom
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import com.patloew.rxlocation.RxLocation
import cz.muni.fi.pv239.drinkup.R
import cz.muni.fi.pv239.drinkup.database.AppDatabase
import cz.muni.fi.pv239.drinkup.database.entity.Category
import cz.muni.fi.pv239.drinkup.database.entity.Drink
import cz.muni.fi.pv239.drinkup.database.entity.DrinkingSession
import cz.muni.fi.pv239.drinkup.service.ComputeBACService.Companion.computeBAC
import cz.muni.fi.pv239.drinkup.service.WearableMessagesListener.Companion.ADD_DRINK_REQUEST_PATH
import cz.muni.fi.pv239.drinkup.service.WearableMessagesListener.Companion.CONFIRM_ADD_DRINK_REQUEST_PATH
import cz.muni.fi.pv239.drinkup.service.WearableMessagesListener.Companion.GET_ALCOHOL_IN_BLOOD_REQUEST_PATH
import cz.muni.fi.pv239.drinkup.service.WearableMessagesListener.Companion.GET_LAST_DRINK_NAME_REQUEST_PATH
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class WearableMessagesListener: WearableListenerService() {

    companion object {
        @JvmStatic val ADD_DRINK_REQUEST_PATH = "/add_drink"
        @JvmStatic val GET_ALCOHOL_IN_BLOOD_REQUEST_PATH = "/alcohol"
        @JvmStatic val GET_LAST_DRINK_NAME_REQUEST_PATH = "/last_drink"
        @JvmStatic val CONFIRM_ADD_DRINK_REQUEST_PATH = "/drink_added"
    }

    private var alcoholCalculationSubscription: Disposable? = null
    private var getLastDrinkSubscription: Disposable? = null
    private var addLastDrinkSubscription: Disposable? = null
    private var locationSubscription: Disposable? = null
    private var db: AppDatabase? = null

    private var latitude: Double? = null
    private var longitute: Double? = null

    override fun onCreate() {
        super.onCreate()
        if ( ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val rxLocation = RxLocation(this)

            val locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)

            locationSubscription = rxLocation.location().updates(locationRequest)
                .flatMap{ rxLocation.geocoding().fromLocation(it).toObservable()}
                .subscribe {
                    latitude = it.latitude
                    longitute = it.longitude
                }
        }
        db = AppDatabase.getAppDatabase(this)
    }

    override fun onDestroy() {
        alcoholCalculationSubscription?.dispose()
        getLastDrinkSubscription?.dispose()
        addLastDrinkSubscription?.dispose()
        locationSubscription?.dispose()
    }

    override fun onMessageReceived(messageEvent: MessageEvent?) {
        when (messageEvent?.path) {
            ADD_DRINK_REQUEST_PATH -> onAddLastDrinkRequestReceived(messageEvent.sourceNodeId)
            GET_ALCOHOL_IN_BLOOD_REQUEST_PATH -> sendAlcoholInBloodInfo(messageEvent.sourceNodeId)
            GET_LAST_DRINK_NAME_REQUEST_PATH -> sendLastDrinkInfo(messageEvent.sourceNodeId)
        }
    }

    private fun onAddLastDrinkRequestReceived(sourceNodeId: String?) {
        addLastDrinkSubscription = addLastDrink()
            .subscribe {
                sourceNodeId?.also { nodeId ->
                    val sendTask: Task<*> = Wearable.getMessageClient(this).sendMessage(
                        nodeId,
                        CONFIRM_ADD_DRINK_REQUEST_PATH,
                        it.toString().toByteArray(Charsets.UTF_8)
                    )
                }
            }
    }

    private fun addLastDrink(): Flowable<AddDrinkOperationResult> {
        return getLastDrink()
            .switchMap { AddDrinkService.addDrink(this, it) }

    }

    private fun sendAlcoholInBloodInfo(sourceNodeId: String?) {
        alcoholCalculationSubscription = calculateAlcoholInBloodInfo()
            .subscribe {
                val alcoholInBloodText = if (it.isNaN()) {
                    getString(R.string.bac_message_no_data)
                } else {
                    "${getString(R.string.you_have)} ${String.format("%.2f", it)} ${getString(R.string.alcohol_promile)}"
                }
                sourceNodeId?.also { nodeId ->
                    val sendTask: Task<*> = Wearable.getMessageClient(this).sendMessage(
                        nodeId,
                        GET_ALCOHOL_IN_BLOOD_REQUEST_PATH,
                        alcoholInBloodText.toByteArray(Charsets.UTF_8)
                    )
                }
            }
    }

    private fun createEmptySession(): DrinkingSession {
        val session = DrinkingSession()
        session.title = "Example session"
        session.created = Date()
        return session
    }

    private fun calculateAlcoholInBloodInfo(): Flowable<Double> {
         return RxRoom.createFlowable(db)
            .observeOn(Schedulers.io())
            .map { db?.sessionDao()?.getLastSession() ?: createEmptySession()}
            .map{
                if (it.id == null) {
                    Collections.emptyList()
                }
                else {
                    db?.sessionDao()?.getAllDrinks(it.id) ?: Collections.emptyList()}
            }
             .map {computeBAC(this, it, false) ?: Double.NaN }
             .onErrorReturn { Double.NaN }
    }


    private fun sendLastDrinkInfo(sourceNodeId: String?) {
        getLastDrinkSubscription = this.getLastDrink()
            .subscribe{
                sourceNodeId?.also { nodeId ->
                    val sendTask: Task<*> = Wearable.getMessageClient(this).sendMessage(
                        nodeId,
                        GET_LAST_DRINK_NAME_REQUEST_PATH,
                        this.getDrinkName(it).toByteArray(Charsets.UTF_8)
                    )
                }
            }
        }

    private fun getDrinkName(drink: Drink): String {
        return if (drink.id == null)
            "beer"
        else
            drink.name
    }

    private fun getLastDrink(): Flowable<Drink> {
        return RxRoom.createFlowable(db)
            .observeOn(Schedulers.io())
            .map { db?.drinkDao()?.getLastDrink() ?: Drink( name = "Default beer", price = 1.0, abv = 4.0, volume = 500.0, category = Category.BEER) }
            .map { it.latitude = latitude ?: 0.0
                   it.longitude = longitute ?: 0.0
                it
            }
    }
}