package cz.muni.fi.pv239.drinkup.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.RxRoom
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
        setContentView(cz.muni.fi.pv239.drinkup.R.layout.activity_drinking_session_detail)
        db = AppDatabase.getAppDatabase(this)

        val session = this.intent.getParcelableExtra<DrinkingSession>(DrinkingSessionsAdapter.INTENT_EXTRA_DRINKING_SESSION)

        adapter = DrinksOfSessionAdapter(this)
        session_drinks.adapter = adapter
        session_drinks.layoutManager = LinearLayoutManager(this)
        showSession(session)

}
    private fun showSession(session: DrinkingSession){
        if (session.id != null) {
            session_title.text = session.title
            session_created.text = session.created.toString("dd-MMM-yyyy hh:mm:ss")
            session_price.text = "0"
            loadDrinks(session.id)
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

}