package cz.muni.fi.pv239.drinkup.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.RxRoom

import cz.muni.fi.pv239.drinkup.R
import cz.muni.fi.pv239.drinkup.activity.AddDrinkActivity
import cz.muni.fi.pv239.drinkup.activity.EditDrinkDefinitionActivity
import cz.muni.fi.pv239.drinkup.adapter.DrinksOfSessionAdapter
import cz.muni.fi.pv239.drinkup.database.AppDatabase
import cz.muni.fi.pv239.drinkup.database.dao.SessionDao
import cz.muni.fi.pv239.drinkup.database.entity.Drink
import cz.muni.fi.pv239.drinkup.database.entity.DrinkingSession
import cz.muni.fi.pv239.drinkup.service.ComputeBACService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import khronos.toString
import kotlinx.android.synthetic.main.activity_drinking_session_detail.*
import kotlinx.android.synthetic.main.fragment_overview.*
import kotlinx.android.synthetic.main.fragment_overview.session_bac
import kotlinx.android.synthetic.main.fragment_overview.session_created
import kotlinx.android.synthetic.main.fragment_overview.session_price
import org.w3c.dom.Text
import java.util.*


class OverviewFragment : Fragment() {

    private lateinit var adapter: DrinksOfSessionAdapter
    private var db: AppDatabase? = null
    private var loadDrinksSubscription: Disposable? = null
    private var loadSessionSubscription: Disposable? = null
    private var getPriceSubscription: Disposable? = null
    private var computeBacSubscription: Disposable? = null

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
            createAddButton(view)
            createEndButton(view, myContext)
            setVisibilities(myContext)
            editButton(view)
        }
    }

    private fun setVisibilities(myContext: Context?){
        if (isActiveSession(myContext)){
            last_session_end_button.visibility = View.VISIBLE
            edit_session_title.visibility = View.VISIBLE

        }else{
            last_session_end_button.visibility = View.INVISIBLE
            edit_session_title.visibility = View.INVISIBLE
        }
    }

    private fun createAddButton(view: View){
        val fab: View = view.findViewById(R.id.last_session_add_fab)
        fab.setOnClickListener {
            val intent = Intent(it.context, AddDrinkActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }

    private fun editButton(view: View) {
        val fab: View = view.findViewById(R.id.edit_session_title)
        fab.setOnClickListener{
            var builder = AlertDialog.Builder(context)
            builder.setTitle(getString(R.string.edit_title))
            val editText = EditText(context)
            builder.setView(editText)
            builder.setPositiveButton(R.string.save) { dialog, which ->
                loadSessionSubscription = RxRoom.createFlowable(db)
                    .observeOn(Schedulers.io())
                    .map{  db?.sessionDao()?.getLastSession() ?: error("error") }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        editTitle(it, editText.text.toString())
                    }
                var tv: TextView = view.findViewById(R.id.session_title_text)
                tv.text = editText.text.toString()
            }
            builder.setNegativeButton(R.string.cancel) { dialog, which ->
                dialog.cancel()
            }
            builder.show()
        }
    }

    private fun editTitle(session: DrinkingSession, title: String) {
        session.title = title
        loadSessionSubscription = RxRoom.createFlowable(db)
            .observeOn(Schedulers.io())
            .map{  db?.sessionDao()?.updateSession(session) ?: error("error") }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    private fun setActive(myContext: Context, value: Boolean) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(myContext)
        sharedPreferences.edit().putBoolean("is_active_session", value).apply()

    }

    private fun createEndButton(view: View, myContext: Context){
        val fab: View = view.findViewById(R.id.last_session_end_button)
        fab.setOnClickListener {
            setActive(myContext,false)
            setVisibilities(context)
        }
    }

    override fun onResume() {
        super.onResume()
        val myContext = context
        if (myContext != null) {
            loadSession(myContext)
            setVisibilities(myContext)
        }
    }

    override fun onPause() {
        super.onPause()
        adapter.notifyDataSetChanged()
        setVisibilities(context)

    }

    private fun isActiveSession(myContext: Context?): Boolean{
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(myContext)
        return sharedPreferences.getBoolean("is_active_session", false)
    }

    private fun loadSession(myContext: Context){
        loadSessionSubscription = RxRoom.createFlowable(db)
                .observeOn(Schedulers.io())
                .map{  db?.sessionDao()?.getLastSession() ?: createInitialSession() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    showSession(it, myContext)
                }
    }

    private fun createInitialSession(): DrinkingSession {
        val session = DrinkingSession()
        session.title = "Example session"
        session.created = Date()
        return session
    }

    private fun showSession(session: DrinkingSession?, myContext: Context){
        if (session?.id != null) {
            //session_title_text.text = session.title
            session_title_text.text = session.title
            session_created.text = session.created.toString("dd-MMM-yyyy HH:mm:ss")
            loadDrinks(session.id)
            computeDrinksBAC(session.id, myContext)
            computePrice(session.id)
        }else {
            setActive(myContext, false)
            session_created.text = getString(R.string.no_session_text)
        }
    }

    private fun computePrice(sId: Long){
        getPriceSubscription = RxRoom.createFlowable(db)
                .observeOn(Schedulers.io())
                .map{db?.sessionDao()?.getAllDrinks(sId) ?: Collections.emptyList()}
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it ->
                    val price = it.sumByDouble{it.price}
                    session_price.text = getString(R.string.total_price_of_session, price)
                }
    }

    private fun loadDrinks(sessionId: Long){
        loadDrinksSubscription = RxRoom.createFlowable(db)
                .observeOn(Schedulers.io())
                .map{db?.sessionDao()?.getAllDrinks(sessionId) ?: Collections.emptyList()}
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    populateList(it)
                }
    }

    private fun populateList(drinks: List<Drink>) {
        Collections.reverse(drinks)
        adapter.refreshDrinks(drinks)
    }

    private fun computeDrinksBAC(sessionId: Long, myContext: Context) {
        computeBacSubscription = RxRoom.createFlowable(db)
                .observeOn(Schedulers.io())
                .map{db?.sessionDao()?.getAllDrinks(sessionId) ?: Collections.emptyList()}
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    computeBAC(it, myContext)
                }
    }

    private fun computeBAC(drinks: List<Drink>, myContext: Context) {
        val bac = ComputeBACService.computeBAC(myContext, drinks, false)
        if (bac == null) {
            session_bac.text = getString(R.string.bac_not_set)
        }
        else {
            session_bac.text = String.format("%s : %.2f‰", getString(R.string.bac), bac)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnOverviewFragmentInteractionListener) {
        } else {
            throw RuntimeException(context.toString() + " must implement OnMyDrinksFragmentInteractionListener")
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        loadDrinksSubscription?.dispose()
        loadSessionSubscription?.dispose()
        getPriceSubscription?.dispose()
        computeBacSubscription?.dispose()
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
