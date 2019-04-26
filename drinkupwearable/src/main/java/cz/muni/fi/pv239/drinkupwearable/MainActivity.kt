package cz.muni.fi.pv239.drinkupwearable

import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.view.View
import androidx.wear.ambient.AmbientModeSupport
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : WearableActivity(),
    AmbientModeSupport.AmbientCallbackProvider,
    CapabilityClient.OnCapabilityChangedListener {

    companion object {
        @JvmStatic val CAPABILITY_PHONE_APP = "verify_remote_drinkup_phone_app"
    }

    private var androidPhoneNodeWithApp: Node? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setAmbientEnabled()
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

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback {
        return MyAmbientCallback()
    }

    override fun onCapabilityChanged(capabilityInfo: CapabilityInfo) {
        androidPhoneNodeWithApp = pickBestNodeId(capabilityInfo.nodes)
        verifyNodeAndUpdateUI()
    }

    private fun pickBestNodeId(nodes: Set<Node>): Node? {
        var bestNodeId: Node? = null
        // Find a nearby node/phone or pick one arbitrarily. Realistically, there is only one phone.
        for (node in nodes) {
            bestNodeId = node
        }
        return bestNodeId
    }

    private fun verifyNodeAndUpdateUI() {
        if (androidPhoneNodeWithApp != null) {
          // TODO: Add code to communicate with phone app via api)
            no_paired_phone_text.visibility = View.INVISIBLE
            main_layout.visibility = View.VISIBLE
        }
        else {
            no_paired_phone_text.visibility = View.VISIBLE
            main_layout.visibility = View.INVISIBLE
        }
    }

    private fun checkIfPhoneHasApp() {
       val capabilityInfoTask: Task<CapabilityInfo> = Wearable.getCapabilityClient(this).getCapability(
           CAPABILITY_PHONE_APP, CapabilityClient.FILTER_ALL)
        capabilityInfoTask.addOnCompleteListener {
            if (it.isSuccessful) {
                val nodes = it.result?.nodes ?: Collections.emptySet()
                androidPhoneNodeWithApp = pickBestNodeId(nodes)
            }
            verifyNodeAndUpdateUI()
        }
    }

    private inner class MyAmbientCallback : AmbientModeSupport.AmbientCallback() {
        /** Prepares the UI for ambient mode.  */
        override fun onEnterAmbient(ambientDetails: Bundle?) {
            super.onEnterAmbient(ambientDetails)
            // In our case, the assets are already in black and white, so we don't update UI.
        }

        /** Restores the UI to active (non-ambient) mode.  */
        override fun onExitAmbient() {
            super.onExitAmbient()
            // In our case, the assets are already in black and white, so we don't update UI.
        }
    }}
