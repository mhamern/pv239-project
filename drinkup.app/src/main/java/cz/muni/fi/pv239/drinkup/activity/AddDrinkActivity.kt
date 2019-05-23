package cz.muni.fi.pv239.drinkup.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.RxRoom
import cz.muni.fi.pv239.drinkup.R
import cz.muni.fi.pv239.drinkup.adapter.DrinkDefinitionsAdapter
import cz.muni.fi.pv239.drinkup.database.AppDatabase
import cz.muni.fi.pv239.drinkup.database.entity.DrinkDefinition
import cz.muni.fi.pv239.drinkup.event.listener.OnEditDrinkDefinitionListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_drink.*
import kotlinx.android.synthetic.main.activity_drinking_session_detail.*
import java.util.*

class AddDrinkActivity: AppCompatActivity(), OnEditDrinkDefinitionListener {
    override fun onEditRequested(editIntent: Intent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

}