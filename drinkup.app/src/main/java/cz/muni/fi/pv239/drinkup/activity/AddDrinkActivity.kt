package cz.muni.fi.pv239.drinkup.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.RxRoom
import cz.muni.fi.pv239.drinkup.R
import cz.muni.fi.pv239.drinkup.adapter.DrinkDefinitionsAdapter
import cz.muni.fi.pv239.drinkup.database.AppDatabase
import cz.muni.fi.pv239.drinkup.database.entity.Category
import cz.muni.fi.pv239.drinkup.database.entity.Drink
import cz.muni.fi.pv239.drinkup.database.entity.DrinkDefinition
import cz.muni.fi.pv239.drinkup.event.listener.OnEditDrinkDefinitionListener
import cz.muni.fi.pv239.drinkup.service.AddDrinkService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_drink.*
import kotlinx.android.synthetic.main.activity_drinking_session_detail.*
import java.util.*

class AddDrinkActivity: AppCompatActivity(), OnEditDrinkDefinitionListener {
    private var saveDrinkSubscription: Disposable? = null


    override fun onEditRequested(editIntent: Intent) {
        val drinkDefToAdd = editIntent.getParcelableExtra<DrinkDefinition>(DrinkDefinitionsAdapter.INTENT_EXTRA_EDIT_DRINK)
        saveDrinkSubscription = AddDrinkService.addDrink(this,
                Drink(name = drinkDefToAdd.name,
                        abv = drinkDefToAdd.abv,
                        volume = drinkDefToAdd.volume.toDouble(),
                        price = drinkDefToAdd.price,
                        category = drinkDefToAdd.category))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        PreferenceManager
                .getDefaultSharedPreferences(this)
                .edit()
                .putBoolean("is_active_session", true)
                .apply()
        achievements(drinkDefToAdd.category)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        saveDrinkSubscription?.dispose()
    }

    private lateinit var adapter: DrinkDefinitionsAdapter
    private var db: AppDatabase? = null
    private var addDrinksSubscription: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_drink)
        db = AppDatabase.getAppDatabase(this)

        adapter = DrinkDefinitionsAdapter(this, this,
                addingDrink = true)
        drink_definitions_list.adapter = adapter
        drink_definitions_list.layoutManager = LinearLayoutManager(this)
        createAppBar()
        loadDrinkDefinitions()

    }

    private fun loadDrinkDefinitions() {
        addDrinksSubscription = RxRoom.createFlowable(db)
                .observeOn(Schedulers.io())
                .map { db?.drinkDefinitionDao()?.getAllDrinkDefinitions() ?: Collections.emptyList() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    populateList(it)
                }
    }

    private fun populateList(drinks: List<DrinkDefinition>) {
        adapter.refreshDrinks(drinks)
    }

    private fun createAppBar() {
        add_drink_toolbar.title = getString(R.string.add_drink)

        setSupportActionBar(add_drink_toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun achievements(category: Category) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sharedPreferences.edit()
        if (category == Category.BEER) {
            var actual: Int = sharedPreferences.getInt("ach_drinkBeer", 0)
            editor.putInt("ach_drinkBeer", actual+1)
        } else if (category == Category.SPIRIT) {
            var actual: Int = sharedPreferences.getInt("ach_drinkShot", 0)
            editor.putInt("ach_drinkShot", actual+1)
        }
        editor.commit()
        makeNotification(category)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (manager.getNotificationChannel("achievement") != null) {
                return
            }

            val name = "achievement"
            val description = "achievement channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("achievement", name, importance)
            channel.description = description
            manager.createNotificationChannel(channel)
        }
    }

    private fun makeNotification(category: Category) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        var beer = sharedPreferences.getInt("ach_drinkBeer", 0)
        var shots = sharedPreferences.getInt("ach_drinkShot", 0)
        var content = ""
        var completed = false
        if (category == Category.BEER) {
            if (beer == 5) {
                content = getString(R.string.ach_drink5beer)
                completed = true
            }
            if (beer == 20) {
                content = getString(R.string.ach_drink20beer)
                completed = true
            }
            if (beer == 100) {
                content = getString(R.string.ach_drink100beer)
                completed = true
            }
        }
        if (category == Category.SPIRIT) {
            if (shots == 5) {
                content = getString(R.string.ach_drink5shots)
                completed = true
            }
            if (shots == 20) {
                content = getString(R.string.ach_drink20shots)
                completed = true
            }
        }
        if (completed) {
            createNotificationChannel()
            val builder = NotificationCompat.Builder(this, "achievement")
                .setSmallIcon(R.drawable.abc_ic_star_black_16dp)
                .setContentTitle(getString(R.string.achievement_completed))
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            val manager = NotificationManagerCompat.from(this)
            manager.notify(1, builder.build())
        }
    }
}