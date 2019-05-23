package cz.muni.fi.pv239.drinkup.service

import androidx.room.RxRoom
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import cz.muni.fi.pv239.drinkup.R
import cz.muni.fi.pv239.drinkup.database.AppDatabase
import cz.muni.fi.pv239.drinkup.database.entity.Drink
import cz.muni.fi.pv239.drinkup.service.ComputeBACService.Companion.computeBAC
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
    private var db: AppDatabase? = null

    override fun onCreate() {
        super.onCreate()
        db = AppDatabase.getAppDatabase(this)

    }

    override fun onDestroy() {
        alcoholCalculationSubscription?.dispose()
        getLastDrinkSubscription?.dispose()
        addLastDrinkSubscription?.dispose()
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
            .map { AddDrinkOperationResult.SUCCEEDED }
                // TODO ADD Service that will get Drink, add it to last session or create new session and add it to it and return AddDrinkOperationResult.
                // Service cannot subscribe to flowable and cannot do any work on UI thread.
    }

    private fun sendAlcoholInBloodInfo(sourceNodeId: String?) {
        alcoholCalculationSubscription = calculateAlcoholInBloodInfo()
            .subscribe {
                val alcoholInBloodText = if (it.isNaN()) {
                    getString(R.string.bac_message_no_data)
                } else {
                    "${getString(R.string.you_have)} $it ${getString(R.string.alcohol_promile)}"
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

    private fun calculateAlcoholInBloodInfo(): Flowable<Double> {
         return RxRoom.createFlowable(db)
            .observeOn(Schedulers.io())
            .map { db?.sessionDao()?.getLastSession() ?: error("No session dao")}
            .map{
                if (it.id == null) {
                    error ("No session")
                }
                else {
                    db?.sessionDao()?.getAllDrinks(it.id) ?: Collections.emptyList()}
            }
             .map {computeBAC(this, it) ?: Double.NaN }
    }


    private fun sendLastDrinkInfo(sourceNodeId: String?) {
        getLastDrinkSubscription = this.getLastDrink()
            .subscribe{
                sourceNodeId?.also { nodeId ->
                    val sendTask: Task<*> = Wearable.getMessageClient(this).sendMessage(
                        nodeId,
                        GET_LAST_DRINK_NAME_REQUEST_PATH,
                        it.name.toByteArray(Charsets.UTF_8)
                    )
                }
            }
        }

    private fun getLastDrink(): Flowable<Drink> {
        return RxRoom.createFlowable(db)
            .observeOn(Schedulers.io())
            .map { db?.drinkDao()?.getLastDrink() ?: error("No last drink") }
    }
}