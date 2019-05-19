package cz.muni.fi.pv239.drinkup.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.RxRoom

import cz.muni.fi.pv239.drinkup.R
import cz.muni.fi.pv239.drinkup.activity.AddDrinkActivity
import cz.muni.fi.pv239.drinkup.activity.EditDrinkDefinitionActivity
import cz.muni.fi.pv239.drinkup.adapter.DrinksOfSessionAdapter
import cz.muni.fi.pv239.drinkup.database.AppDatabase
import cz.muni.fi.pv239.drinkup.database.entity.Drink
import cz.muni.fi.pv239.drinkup.database.entity.DrinkingSession
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import khronos.toString
import kotlinx.android.synthetic.main.fragment_overview.*
import java.util.*


class OverviewFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val myContext = context
        if (myContext != null) {
            db = AppDatabase.getAppDatabase(myContext)

            adapter = DrinksOfSessionAdapter(myContext)
            last_session_drink_list.adapter = adapter
            last_session_drink_list.layoutManager = LinearLayoutManager(myContext)
            loadSession()
            createAddButton(view)
            createEndButton(view)

            if (!isActiveSession(myContext)){
                val endButton: View = view.findViewById(R.id.last_session_end_button)
                endButton.visibility = View.INVISIBLE
//                val title: View = view.findViewById(R.id.session_title)
//                title.setOnClickListener {  }
//                session_title_text.visibility = View.VISIBLE
//                session_title_editText.visibility = View.GONE
            }
        }
    }

    private fun createAddButton(view: View){
        val fab: View = view.findViewById(R.id.last_session_add_fab)
        fab.setOnClickListener {
            val intent = Intent(it.context, AddDrinkActivity::class.java)
            startActivityForResult(intent, 1)
            setActive(true)
        }
    }

    private fun setActive(value: Boolean) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().putBoolean("is_active_session", value).apply()
        fragmentManager
                ?.beginTransaction()
                ?.detach(this)
                ?.attach(this)
                ?.commit()
    }

    private fun createEndButton(view: View){
        val fab: View = view.findViewById(R.id.last_session_end_button)
        fab.setOnClickListener {
            Toast.makeText(context, "End button", Toast.LENGTH_SHORT).show()
            setActive(false)
        }
    }

    private fun isActiveSession(myContext: Context): Boolean{
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(myContext)
        return sharedPreferences.getBoolean("is_active_session", false)
    }

    private fun loadSession(){
        getDrinksSubscription = RxRoom.createFlowable(db)
                .observeOn(Schedulers.io())
                .map{db?.sessionDao()?.getLastSession() ?: error("error")}
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    showSession(it)
                }
    }

    private fun showSession(session: DrinkingSession?){
        if (session?.id != null) {
            session_title_text.text = session.title
            session_title_text.text = session.title
            session_created.text = session.created.toString("dd-MMM-yyyy HH:mm:ss")
            loadDrinks(session.id)
            computeDrinksBAC(session.id)
            computePrice(session.id)
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnOverviewFragmentInteractionListener) {
        } else {
            throw RuntimeException(context.toString() + " must implement OnMyDrinksFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnOverviewFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onOverviewFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OverviewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OverviewFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
