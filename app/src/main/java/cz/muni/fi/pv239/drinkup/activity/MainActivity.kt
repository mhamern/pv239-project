package cz.muni.fi.pv239.drinkup.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
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
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable


class MainActivity : AppCompatActivity(),
    StatisticsFragment.OnStatisticsFragmentInteractionListener,
    OverviewFragment.OnOverviewFragmentInteractionListener,
    HistoryFragment.OnHistoryFragmentInteractionListener,
    AchievementsFragment.OnAchievementsFragmentInteractionListener,
    MyDrinksFragment.OnMyDrinksFragmentInteractionListener,
    CapabilityClient.OnCapabilityChangedListener {

    private var allConnectedNodes: List<Node> = Collections.emptyList()
    private var wearNodesWithApp: Set<Node> = Collections.emptySet()

    private lateinit var drawerLayout: DrawerLayout

    companion object {
        @JvmStatic val CAPABILITY_WEAR_APP = "verify_remote_drinkup_wear_app"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            createDrawer()
            createAppBar()
            showOverview()
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
                showOverview()
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

    private fun showOverview() {
        setActiveFragment(OverviewFragment())
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
            // TODO: Communicate with wear app
        }
    }
}
