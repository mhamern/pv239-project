package cz.muni.fi.pv239.drinkup.activity

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.RxRoom
import cz.muni.fi.pv239.drinkup.R
import cz.muni.fi.pv239.drinkup.adapter.DrinkingSessionsAdapter
import cz.muni.fi.pv239.drinkup.adapter.DrinksOfSessionAdapter
import cz.muni.fi.pv239.drinkup.database.AppDatabase
import cz.muni.fi.pv239.drinkup.database.entity.Drink
import cz.muni.fi.pv239.drinkup.database.entity.DrinkingSession
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import khronos.toString
import kotlinx.android.synthetic.main.activity_drinking_session_detail.*
import java.util.*

class DrinkingSessionDetailActivity: AppCompatActivity(){

    private lateinit var adapter: DrinksOfSessionAdapter
    private var db: AppDatabase? = null
    private var getDrinksSubscription: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drinking_session_detail)
        db = AppDatabase.getAppDatabase(this)

        val session = this.intent.getParcelableExtra<DrinkingSession>(DrinkingSessionsAdapter.INTENT_EXTRA_DRINKING_SESSION)

        adapter = DrinksOfSessionAdapter(this)
        session_drinks.adapter = adapter
        session_drinks.layoutManager = LinearLayoutManager(this)
        showSession(session)
        show_map.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("sessionid", session.id)
            startActivity(intent)
        }
        createAppBar()

    }

    private fun createAppBar() {
        drinking_session_toolbar.title = getString(R.string.session_detail)

        setSupportActionBar(drinking_session_toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun showSession(session: DrinkingSession){
        if (session.id != null) {
            session_title.text = session.title
            session_created.text = session.created.toString("dd-MMM-yyyy HH:mm:ss")
            computePrice(session.id)
            session_price.text = "0"
            loadDrinks(session.id)
            computeDrinksBAC(session.id)
        }
    }

    private fun computePrice(sId: Long){
        getDrinksSubscription = RxRoom.createFlowable(db)
                .observeOn(Schedulers.io())
                .map{db?.sessionDao()?.getAllDrinks(sId) ?: Collections.emptyList()}
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it ->
                    val price = it.sumByDouble{it.price}
                    session_price.text = getString(R.string.total_price_of_session, price)
                }
    }

    private fun loadDrinks(sessionId: Long){
        getDrinksSubscription = RxRoom.createFlowable(db)
                .observeOn(Schedulers.io())
                .map{db?.sessionDao()?.getAllDrinks(sessionId) ?: Collections.emptyList()}
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    populateList(it)
                }
    }

    private fun populateList(drinks: List<Drink>) {
        adapter.refreshDrinks(drinks)
    }

    private fun computeDrinksBAC(sessionId: Long) {
        getDrinksSubscription = RxRoom.createFlowable(db)
            .observeOn(Schedulers.io())
            .map{db?.sessionDao()?.getAllDrinks(sessionId) ?: Collections.emptyList()}
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                computeBAC(it)
            }
    }

    private fun computeBAC(drinks: List<Drink>) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        var weightSP = sharedPreferences.getString("pref_weight", "")
        var genderSP = sharedPreferences.getString("pref_gender", "")
        var weight: Double
        var gender: Int
        if (weightSP == "" || genderSP == "") {
            session_bac.text = getString(R.string.bac_not_set)
            return
        } else {
            weight = weightSP.toDouble()
            gender = genderSP.toInt()
        }
        var genderConst: Double
        if(gender == 0) {
            genderConst = 0.68
        } else {
            genderConst = 0.55
        }
        var sortedList = drinks.sortedWith(compareBy({ it.date }))
        var time: Double = (sortedList.last().date.time - sortedList.first().date.time).toDouble()/1000/60/60
        var goa = 0.0
        for(drink in drinks) {
            goa = goa + (drink.volume*(drink.abv/100)*0.789)
        }
        var bac = ((goa/(weight*1000*genderConst))*100 - time*0.015)*10
        session_bac.text = String.format("%s : %.2f‰", getString(R.string.bac), bac)
    }



}