package cz.muni.fi.pv239.drinkup.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import cz.muni.fi.pv239.drinkup.R
import cz.muni.fi.pv239.drinkup.fragment.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.*
import cz.muni.fi.pv239.drinkup.fragment.statistics.StatisticsFragment
import com.google.android.gms.wearable.Wearable




class MainActivity : AppCompatActivity(),
    StatisticsFragment.OnStatisticsFragmentInteractionListener,
    OverviewFragment.OnOverviewFragmentInteractionListener,
    HistoryFragment.OnHistoryFragmentInteractionListener,
    AchievementsFragment.OnAchievementsFragmentInteractionListener,
    MyDrinksFragment.OnMyDrinksFragmentInteractionListener,
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener {

    private val TAG = "MainActivity"

    private var allConnectedNodes: List<Node> = Collections.emptyList()
    private var wearNodesWithApp: Set<Node> = Collections.emptySet()

    private lateinit var drawerLayout: DrawerLayout

    companion object {
        @JvmStatic val CAPABILITY_WEAR_APP = "watch_client"
        @JvmStatic val ADD_DRINK_REQUEST_PATH = "/add_drink"
        @JvmStatic val GET_ALCOHOL_IN_BLOOD_REQUEST_PATH = "/alcohol"
        @JvmStatic val GET_LAST_DRINK_NAME_REQUEST_PATH = "/last_drink"
        @JvmStatic val CONFIRM_ADD_DRINK_REQUEST_PATH = "/drink_added"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createDrawer()
        createAppBar()
        Wearable.getCapabilityClient(this).addListener(this, CAPABILITY_WEAR_APP)
        findWearDevicesWithApp()
        findAllWearDevices()
        if (savedInstanceState == null) {
            setPreferences()
            if (isActiveSession()){
                showActive()
            }else{
                showLast()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Wearable.getCapabilityClient(this).removeListener(this, CAPABILITY_WEAR_APP)
    }

    override fun onResume() {
        super.onResume()
        Wearable.getCapabilityClient(this).addListener(this, CAPABILITY_WEAR_APP)
        findWearDevicesWithApp()
        findAllWearDevices()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCapabilityChanged(capabilityInfo: CapabilityInfo) {
        wearNodesWithApp = capabilityInfo.nodes
        findAllWearDevices()
        verifyNodeAndStartCommunication()
    }

    override fun onOverviewFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStatisticsFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onHistoryFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAchievementsFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMyDrinksFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == ADD_DRINK_REQUEST_PATH) {
            Log.i(TAG, "Received add message event from wearable ${messageEvent.sourceNodeId}")
            // TODO: start session and add favourite drink OR add last drink to existing session
        }
    }

    private fun createDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            handleNavMenuItemSelection(menuItem)
        }
    }

    private fun createAppBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }
    }

    private fun handleNavMenuItemSelection(menuItem: MenuItem): Boolean {
        menuItem.isChecked = true
        drawerLayout.closeDrawers()
        return when (menuItem.itemId) {
            R.id.nav_overview -> {
                if (isActiveSession()){
                    showActive()
                }else{
                    showLast()
                }
                true
            }
            R.id.nav_my_drinks -> {
                showMyDrinks()
                true
            }
            R.id.nav_history -> {
                showHistory()
                true
            }
            R.id.nav_achievements -> {
                showAchievements()
                true
            }
            R.id.nav_statistics -> {
                showStatistics()
                true
            }
            R.id.nav_settings -> {
                showSettings()
                true
            }
            else -> true
        }
    }

    private fun showMyDrinks() {
        setActiveFragment(MyDrinksFragment())
    }

    private fun showAchievements() {
        setActiveFragment(AchievementsFragment())
    }

    private fun showStatistics() {
        setActiveFragment(StatisticsFragment())
    }

    private fun showHistory() {
        setActiveFragment(HistoryFragment())
    }

    private fun showSettings() {
        this.startActivity(Intent(this, SettingsActivity::class.java))
    }

    private fun showActive(){
        setActiveFragment(ActiveSessionFragment())
    }

    private fun showLast(){
        setActiveFragment(LastSessionFragment())
    }

    private fun setActiveFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun findAllWearDevices() {
        val nodeListTask: Task<List<Node>> = Wearable.getNodeClient(this).connectedNodes
        nodeListTask.addOnCompleteListener {
            if (it.isSuccessful) {
                allConnectedNodes = it.result ?: Collections.emptyList()
            }
        }
    }

    private fun findWearDevicesWithApp() {
        val capabilityInfoTask: Task<CapabilityInfo> = Wearable.getCapabilityClient(this)
            .getCapability(CAPABILITY_WEAR_APP, CapabilityClient.FILTER_ALL)
        capabilityInfoTask.addOnCompleteListener {
            if (it.isSuccessful) {
                val capabilityInfo: CapabilityInfo? = it.result
                wearNodesWithApp = capabilityInfo?.nodes ?: Collections.emptySet()
                verifyNodeAndStartCommunication()
            }
        }
    }

    private fun verifyNodeAndStartCommunication() {
        if (!allConnectedNodes.isEmpty() && !wearNodesWithApp.isEmpty()) {
            Wearable.getMessageClient(this).addListener { messageEvent -> handleMessage(messageEvent) }
        }
    }

    private fun handleMessage(messageEvent: MessageEvent) {
        when (messageEvent.path) {
            ADD_DRINK_REQUEST_PATH -> onAddLastDrinkRequestReceived(messageEvent.sourceNodeId)
            GET_ALCOHOL_IN_BLOOD_REQUEST_PATH -> sendAlcoholInBloodInfo(messageEvent.sourceNodeId)
            GET_LAST_DRINK_NAME_REQUEST_PATH -> sendLastDrinkInfo(messageEvent.sourceNodeId)
        }
    }

    private fun onAddLastDrinkRequestReceived(sourceNodeId: String?) {
        // TODO RETRIEVE FROM DB AND ADD
        confirmDrinkAdded(sourceNodeId)
        Toast.makeText(this, getString(R.string.drink_added_from_watch_toast), Toast.LENGTH_SHORT).show()}

    private fun confirmDrinkAdded(sourceNodeId: String?) {
        sourceNodeId?.also { nodeId ->
            val sendTask: Task<*> = Wearable.getMessageClient(this).sendMessage(
                nodeId,
                CONFIRM_ADD_DRINK_REQUEST_PATH,
                null
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



    private fun setPreferences() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (sharedPreferences.getBoolean("firstRun", true)) {
            val editor = sharedPreferences.edit()
            editor.putBoolean("firstRun", false)

            editor.putInt("ach_drink5beer", 0)

            editor.putInt("ach_drink20beer", 0)

            editor.putInt("ach_drink5shots", 0)

            editor.putInt("ach_drink20shots", 0)

            editor.putInt("ach_drink100beer", 0)

            editor.putBoolean("is_active_session", false)

            editor.commit()
        }
    }

    private fun isActiveSession(): Boolean{
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        return sharedPreferences.getBoolean("is_active_session", false)
    }

}
