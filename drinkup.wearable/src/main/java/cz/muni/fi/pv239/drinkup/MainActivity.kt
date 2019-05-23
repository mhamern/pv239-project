package cz.muni.fi.pv239.drinkup

import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.view.View
import android.widget.Toast
import androidx.wear.ambient.AmbientModeSupport
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : WearableActivity(),
    CapabilityClient.OnCapabilityChangedListener {

    companion object {
        @JvmStatic
        val CAPABILITY_PHONE_APP = "watch_server"
        @JvmStatic
        val ADD_DRINK_MESSAGE_PATH = "/add_drink"
        @JvmStatic
        val GET_ALCOHOL_IN_BLOOD_REQUEST_PATH = "/alcohol"
        @JvmStatic
        val GET_LAST_DRINK_NAME_REQUEST_PATH = "/last_drink"
        @JvmStatic
        val CONFIRM_ADD_DRINK_REQUEST_PATH = "/drink_added"
    }

    private var androidPhoneNodeWithApp: Node? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setAmbientEnabled()
        initAddDrinkButton()
    }

    override fun onPause() {
        super.onPause()
        Wearable.getCapabilityClient(this).removeListener(this, CAPABILITY_PHONE_APP)
    }

    override fun onResume() {
        super.onResume()
        Wearable.getCapabilityClient(this).addListener(this, CAPABILITY_PHONE_APP)
        checkIfPhoneHasApp()
    }


    override fun onCapabilityChanged(capabilityInfo: CapabilityInfo) {
        androidPhoneNodeWithApp = pickBestNodeId(capabilityInfo.nodes)
        verifyNodeAndUpdateUI()
        startCommunication()
    }

    private fun startCommunication() {
        if (androidPhoneNodeWithApp != null) {
            Wearable.getMessageClient(this).addListener { messageEvent -> handleMessage(messageEvent) }
            val id = androidPhoneNodeWithApp?.id
            if (id != null) {
                requestGetAlcoholInBlood(id)
                requestGetLastDrink(id)
            }
        }
    }

    private fun handleMessage(messageEvent: MessageEvent) {
        when (messageEvent.path) {
            CONFIRM_ADD_DRINK_REQUEST_PATH -> onAddDrinkConfirmation(messageEvent)
            GET_LAST_DRINK_NAME_REQUEST_PATH -> onLastDrinkNameUpdated(messageEvent)
            GET_ALCOHOL_IN_BLOOD_REQUEST_PATH -> onAlcoholInBloodUpdated(messageEvent)
        }
    }

    private fun pickBestNodeId(nodes: Set<Node>): Node? {
        var bestNodeId: Node? = null
        for (node in nodes) {
            bestNodeId = node
        }
        return bestNodeId
    }

    private fun verifyNodeAndUpdateUI() {
        if (androidPhoneNodeWithApp != null) {
            no_paired_phone_text.visibility = View.INVISIBLE
            add_drink_button.visibility = View.VISIBLE
            alcohol_in_blood_text.visibility = View.VISIBLE
        } else {
            no_paired_phone_text.visibility = View.VISIBLE
            add_drink_button.visibility = View.INVISIBLE
            alcohol_in_blood_text.visibility = View.INVISIBLE
        }
    }


    private fun checkIfPhoneHasApp() {
        val capabilityInfoTask = Wearable.getCapabilityClient(this)
            .getCapability(CAPABILITY_PHONE_APP, CapabilityClient.FILTER_REACHABLE)

        capabilityInfoTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val capabilityInfo = task.result
                val nodes = capabilityInfo!!.nodes
                androidPhoneNodeWithApp = pickBestNodeId(nodes)
            }
            verifyNodeAndUpdateUI()
            startCommunication()
        }
    }

    private fun initAddDrinkButton() {
        add_drink_button.setOnClickListener {
            val id = androidPhoneNodeWithApp?.id
            if (id != null)
                requestAddDrink(id)
        }
    }

    private fun requestAddDrink(id: String) {
        id.also { nodeId ->
            val sendTask: Task<*> = Wearable.getMessageClient(this).sendMessage(
                nodeId,
                ADD_DRINK_MESSAGE_PATH,
                null
            ).apply {
                addOnSuccessListener {
                }
                addOnFailureListener {
                    onAddDrinkFailure()
                }
            }
        }
    }

    private fun requestGetLastDrink(id: String) {
        id.also { nodeId ->
            val sendTask: Task<*> = Wearable.getMessageClient(this).sendMessage(
                nodeId,
                GET_LAST_DRINK_NAME_REQUEST_PATH,
                null
            )
        }
    }

    private fun requestGetAlcoholInBlood(id: String) {
        id.also { nodeId ->
            val sendTask: Task<*> = Wearable.getMessageClient(this).sendMessage(
                nodeId,
                GET_ALCOHOL_IN_BLOOD_REQUEST_PATH,
                null
            )
        }
    }


    private fun onAlcoholInBloodUpdated(messageEvent: MessageEvent) {
        alcohol_in_blood_text.text = messageEvent.data.toString(Charsets.UTF_8)
    }

    private fun onLastDrinkNameUpdated(messageEvent: MessageEvent) {
        val lastDrinkName = messageEvent.data.toString(Charsets.UTF_8)
        add_drink_button.text = getString(R.string.add_last_drink_parameterized, lastDrinkName)
    }

    private fun onAddDrinkConfirmation(messageEvent: MessageEvent) {
        when (messageEvent.data.toString(Charsets.UTF_8)) {
            "success" -> Toast.makeText(this, getString(R.string.drink_added_toast), Toast.LENGTH_SHORT).show()
            "success_no_permission" -> Toast.makeText(
                this,
                getString(R.string.drink_added_no_permission_toast),
                Toast.LENGTH_SHORT
            ).show()
            "failure" -> Toast.makeText(this, getString(R.string.drink_add_failed), Toast.LENGTH_SHORT).show()

        }
    }

    private fun onAddDrinkFailure() {
        Toast.makeText(this, "Adding drink failed. Check you connection.", Toast.LENGTH_LONG).show()
    }
}