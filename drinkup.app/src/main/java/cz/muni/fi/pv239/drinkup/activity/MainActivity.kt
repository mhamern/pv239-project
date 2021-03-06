package cz.muni.fi.pv239.drinkup.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.room.RxRoom
import com.google.android.material.navigation.NavigationView
import cz.muni.fi.pv239.drinkup.R
import cz.muni.fi.pv239.drinkup.fragment.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.*
import cz.muni.fi.pv239.drinkup.fragment.statistics.StatisticsFragment
import com.google.android.gms.wearable.Wearable
import cz.muni.fi.pv239.drinkup.database.AppDatabase
import cz.muni.fi.pv239.drinkup.database.entity.Category
import cz.muni.fi.pv239.drinkup.database.entity.Drink
import cz.muni.fi.pv239.drinkup.database.entity.DrinkingSession
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import android.provider.Settings
import cz.muni.fi.pv239.drinkup.database.entity.DrinkDefinition
import io.reactivex.disposables.Disposable


class MainActivity : AppCompatActivity(),
    StatisticsFragment.OnStatisticsFragmentInteractionListener,
    OverviewFragment.OnOverviewFragmentInteractionListener,
    HistoryFragment.OnHistoryFragmentInteractionListener,
    AchievementsFragment.OnAchievementsFragmentInteractionListener,
    MyDrinksFragment.OnMyDrinksFragmentInteractionListener {

    private lateinit var drawerLayout: DrawerLayout

    private val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    private val COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
    private val LOCATION_PERMISSION_REQUEST_CODE = 1234
    private var mLocationPermissionsGranted = false

    private var db: AppDatabase? = null
    private var addDrinkDefinitionSubscription: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createDrawer()
        createAppBar()

        if (savedInstanceState == null) {
            getLocationPermission()
            setPreferences()
            showOverview()
        }
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

    private fun createFirstDrinkDefinition(){
        db = AppDatabase.getAppDatabase(this)
        addDrinkDefinitionSubscription = RxRoom.createFlowable(db)
                .observeOn(Schedulers.io())
                .map { db?.drinkDefinitionDao()?.insertDrinkDefiniton(
                        DrinkDefinition(name = "Beer",
                                price = 1.0,
                                volume = 500,
                                abv = 5.0)
                )
                        ?: error("cannot get drikDef dao") }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {}
    }

    private fun setPreferences() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (sharedPreferences.getBoolean("firstRun", true)) {
            createFirstDrinkDefinition()
            val editor = sharedPreferences.edit()
            editor.putBoolean("firstRun", false)

            editor.putInt("ach_drinkBeer", 0)

            editor.putInt("ach_drinkSpirit", 0)

            editor.putBoolean("is_active_session", false)

            editor.commit()
        }
    }

    private fun getLocationPermission() {
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mLocationPermissionsGranted = true
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                showLocationPermissionRationale(this)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun showLocationPermissionRationale(context: Context) {
        val alertBuilder = AlertDialog.Builder(context)
        alertBuilder.setCancelable(true)
        alertBuilder.setTitle(R.string.permission_title)
        alertBuilder.setMessage(R.string.permission_text)
        alertBuilder.setPositiveButton(android.R.string.ok) { dialog, which ->
            // this way you can get to the screen to set the permissions manually
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        val alert = alertBuilder.create()
        alert.show()
    }
}
