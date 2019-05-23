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


class MainActivity : AppCompatActivity(),
    StatisticsFragment.OnStatisticsFragmentInteractionListener,
    OverviewFragment.OnOverviewFragmentInteractionListener,
    HistoryFragment.OnHistoryFragmentInteractionListener,
    AchievementsFragment.OnAchievementsFragmentInteractionListener,
    MyDrinksFragment.OnMyDrinksFragmentInteractionListener {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createDrawer()
        createAppBar()
        if (savedInstanceState == null) {
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
}
