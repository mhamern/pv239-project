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
import cz.muni.fi.pv239.drinkup.database.entity.Category
import cz.muni.fi.pv239.drinkup.database.entity.DrinkDefinition
import kotlinx.android.synthetic.main.fragment_my_drinks.*
import android.app.Activity
import cz.muni.fi.pv239.drinkup.event.listener.EditDrinkDefinitionListener

class MyDrinksFragment : Fragment(), EditDrinkDefinitionListener {

    private lateinit var adapter: DrinkDefinitionsAdapter

    private var listenerMyDrinks: OnMyDrinksFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(cz.muni.fi.pv239.drinkup.R.layout.fragment_my_drinks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val myContext = context
            if (myContext != null) {
                adapter = DrinkDefinitionsAdapter(myContext, this)
                my_drinks_list.adapter = adapter
                my_drinks_list.layoutManager = LinearLayoutManager(context)
                loadDrinkDefinitions()
                val fab: View = view.findViewById(cz.muni.fi.pv239.drinkup.R.id.my_drinks_create_fab)
                fab.setOnClickListener {
                    val intent = Intent(it.context, EditDrinkDefinitionActivity::class.java)
                    startActivityForResult(intent, 1)
                }
            }
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

    override fun onEditRequested(editIntent: Intent) {
        startActivityForResult(editIntent, 1)

    }


    private fun loadDrinkDefinitions() {
        // todo load from DB
        val drink1 = DrinkDefinition()
        drink1.name = "My beer"
        drink1.category = Category.BEER
        drink1.abv = 4.0
        drink1.price = 1.2
        drink1.volume = 500
        val drink2 = DrinkDefinition()
        drink2.name = "My wine"
        drink2.category = Category.WINE
        drink2.abv = 12.0
        drink2.price = 1.5
        drink2.volume = 200
        val drink3 = DrinkDefinition()
        drink3.name = "My shot"
        drink3.category = Category.COCKTAIL
        drink3.abv = 42.0
        drink3.price = 3.5
        drink3.volume = 20
        var drinks = listOf(
            drink1,
            drink2,
            drink3,
            drink1,
            drink2,
            drink3
        )
        populateList(drinks)
    }

    private fun populateList(drinks: List<DrinkDefinition>?) {
        if (drinks == null) {
            return
        }
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
