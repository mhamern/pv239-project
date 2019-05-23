package cz.muni.fi.pv239.drinkup.service

import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import cz.muni.fi.pv239.drinkup.R

class WearableMessagesListener: WearableListenerService() {

    companion object {
        @JvmStatic val ADD_DRINK_REQUEST_PATH = "/add_drink"
        @JvmStatic val GET_ALCOHOL_IN_BLOOD_REQUEST_PATH = "/alcohol"
        @JvmStatic val GET_LAST_DRINK_NAME_REQUEST_PATH = "/last_drink"
        @JvmStatic val CONFIRM_ADD_DRINK_REQUEST_PATH = "/drink_added"
    }

    override fun onMessageReceived(messageEvent: MessageEvent?) {
        when (messageEvent?.path) {
            ADD_DRINK_REQUEST_PATH -> onAddLastDrinkRequestReceived(messageEvent.sourceNodeId)
            GET_ALCOHOL_IN_BLOOD_REQUEST_PATH -> sendAlcoholInBloodInfo(messageEvent.sourceNodeId)
            GET_LAST_DRINK_NAME_REQUEST_PATH -> sendLastDrinkInfo(messageEvent.sourceNodeId)
        }
    }

        private fun onAddLastDrinkRequestReceived(sourceNodeId: String?) {
            confirmDrinkAddedOperation(sourceNodeId, AddDrinkOperationResult.SUCCEEDED)
            Toast.makeText(this, getString(R.string.drink_added_from_watch_toast), Toast.LENGTH_SHORT).show()}

        private fun confirmDrinkAddedOperation(sourceNodeId: String?, operationResult: AddDrinkOperationResult) {
            sourceNodeId?.also { nodeId ->
                val sendTask: Task<*> = Wearable.getMessageClient(this).sendMessage(
                    nodeId,
                    CONFIRM_ADD_DRINK_REQUEST_PATH,
                    operationResult.toString().toByteArray(Charsets.UTF_8)
                )
            }
        }

        private fun sendAlcoholInBloodInfo(sourceNodeId: String?) {
            sourceNodeId?.also { nodeId ->
                val sendTask: Task<*> = Wearable.getMessageClient(this).sendMessage(
                    nodeId,
                    GET_ALCOHOL_IN_BLOOD_REQUEST_PATH,
                    0.94.toString().toByteArray(Charsets.UTF_8)
                )
            }
        }

        private fun sendLastDrinkInfo(sourceNodeId: String?) {
            sourceNodeId?.also { nodeId ->
                val sendTask: Task<*> = Wearable.getMessageClient(this).sendMessage(
                    nodeId,
                    GET_LAST_DRINK_NAME_REQUEST_PATH,
                    "My beer".toByteArray(Charsets.UTF_8)
                )
            }
        }
}