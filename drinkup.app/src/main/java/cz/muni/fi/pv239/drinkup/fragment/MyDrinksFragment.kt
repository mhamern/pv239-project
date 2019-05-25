package cz.muni.fi.pv239.drinkup.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import cz.muni.fi.pv239.drinkup.activity.EditDrinkDefinitionActivity
import cz.muni.fi.pv239.drinkup.adapter.DrinkDefinitionsAdapter
import cz.muni.fi.pv239.drinkup.database.entity.DrinkDefinition
import kotlinx.android.synthetic.main.fragment_my_drinks.*
import android.app.Activity
import androidx.room.RxRoom
import cz.muni.fi.pv239.drinkup.database.AppDatabase
import cz.muni.fi.pv239.drinkup.database.dao.DrinkDefinitionDao
import cz.muni.fi.pv239.drinkup.event.listener.OnEditDrinkDefinitionListener
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

import java.util.*

class MyDrinksFragment : Fragment(), OnEditDrinkDefinitionListener {

    private lateinit var adapter: DrinkDefinitionsAdapter

    private var listenerMyDrinks: OnMyDrinksFragmentInteractionListener? = null
    private var db: AppDatabase? = null
    private var drinkDefDao: DrinkDefinitionDao? = null

    private var loadDrinkDefsSubscription: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(cz.muni.fi.pv239.drinkup.R.layout.fragment_my_drinks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            val myContext = context
            if (myContext != null) {
                initDb(myContext)
                createDrinksList(myContext)
                loadDrinkDefinitions()
                createAddButton(view)
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnMyDrinksFragmentInteractionListener) {
            listenerMyDrinks = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnMyDrinksFragmentInteractionListener")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            loadDrinkDefinitions()
        }
    }

    override fun onDetach() {
        super.onDetach()
        listenerMyDrinks = null
    }

    override fun onDestroy() {
        super.onDestroy()
        loadDrinkDefsSubscription?.dispose()
    }

    override fun onEditRequested(editIntent: Intent) {
        startActivityForResult(editIntent, 1)
    }


    private fun initDb(context: Context) {
        db = AppDatabase.getAppDatabase(context)
        drinkDefDao = db?.drinkDefinitionDao()
    }

    private fun createDrinksList(context: Context) {
        adapter = DrinkDefinitionsAdapter(context, this)
        my_drinks_list.adapter = adapter
        my_drinks_list.layoutManager = LinearLayoutManager(context)
    }

    private fun createAddButton(view: View) {
        val fab: View = view.findViewById(cz.muni.fi.pv239.drinkup.R.id.my_drinks_create_fab)
        fab.setOnClickListener {
            val intent = Intent(it.context, EditDrinkDefinitionActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }

    private fun loadDrinkDefinitions() {
        loadDrinkDefsSubscription = RxRoom.createFlowable(db)
            .observeOn(Schedulers.io())
            .map { db?.drinkDefinitionDao()?.getAllDrinkDefinitions() ?: Collections.emptyList() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                populateList(it)
            }
    }

    private fun populateList(drinks: List<DrinkDefinition>) {
        Collections.reverse(drinks)
        adapter.refreshDrinks(drinks)
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
    interface OnMyDrinksFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onMyDrinksFragmentInteraction(uri: Uri)
    }

    companion object {
        fun newInstance(): MyDrinksFragment {
            return MyDrinksFragment()
        }
    }
}
