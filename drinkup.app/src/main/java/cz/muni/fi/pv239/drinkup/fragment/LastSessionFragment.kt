package cz.muni.fi.pv239.drinkup.fragment

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.RxRoom
import cz.muni.fi.pv239.drinkup.R
import cz.muni.fi.pv239.drinkup.activity.MapActivity
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
import kotlinx.android.synthetic.main.activity_drinking_session_detail.session_bac
import kotlinx.android.synthetic.main.activity_drinking_session_detail.session_created
import kotlinx.android.synthetic.main.activity_drinking_session_detail.session_price
import kotlinx.android.synthetic.main.activity_drinking_session_detail.session_title
import kotlinx.android.synthetic.main.fragment_last_session.*
import java.util.*

class LastSessionFragment: Fragment(){

    private lateinit var adapter: DrinksOfSessionAdapter
    private var db: AppDatabase? = null
    private var getDrinksSubscription: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.retainInstance = true

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_last_session, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val myContext = context
        if (myContext != null) {
            db = AppDatabase.getAppDatabase(myContext)

            adapter = DrinksOfSessionAdapter(myContext)
            last_session_drink_list.adapter = adapter
            last_session_drink_list.layoutManager = LinearLayoutManager(myContext)
            session_created.text = "No session"
            loadSession()
        }
    }

    private fun loadSession(){
        getDrinksSubscription = RxRoom.createFlowable(db)
                .observeOn(Schedulers.io())
                .map{db?.sessionDao()?.getNumOfSessions() ?: 0}
                .map{if (it!=0){showSession(db?.sessionDao()?.getLastSession())}}
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {}
    }

    private fun showSession(session: DrinkingSession?){
        if (session?.id != null) {
            session_title.text = session.title
            session_created.text = session.created.toString("dd-MMM-yyyy hh:mm:ss")
            session_price.text = "0"
            loadDrinks(session.id)
            computeDrinksBAC(session.id)
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
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
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