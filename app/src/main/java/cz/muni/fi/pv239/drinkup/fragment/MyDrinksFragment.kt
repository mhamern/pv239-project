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
import androidx.recyclerview.widget.RecyclerView

import cz.muni.fi.pv239.drinkup.R
import cz.muni.fi.pv239.drinkup.activity.EditDrinkActivity
import cz.muni.fi.pv239.drinkup.adapters.DrinkDefinitionsAdapter
import cz.muni.fi.pv239.drinkup.database.entity.Category
import cz.muni.fi.pv239.drinkup.database.entity.DrinkDefinition
import kotlinx.android.synthetic.main.fragment_my_drinks.*


class MyDrinksFragment : Fragment() {

    private lateinit var adapter: DrinkDefinitionsAdapter

    private var listenerMyDrinks: OnMyDrinksFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_drinks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val myContext = context
        if (myContext != null) {
            adapter = DrinkDefinitionsAdapter(myContext)
            my_drinks_list.adapter = adapter
            my_drinks_list.layoutManager = LinearLayoutManager(context)
            loadDrinkDefinitions()
            my_drinks_create_button.setOnClickListener {
                val intent = Intent(it.context, EditDrinkActivity::class.java)
                it.context.startActivity(intent)
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

    override fun onDetach() {
        super.onDetach()
        listenerMyDrinks = null
    }


    private fun loadDrinkDefinitions() {
        // todo load from DB
        val drink1 = DrinkDefinition()
        drink1.name = "My beer"
        drink1.category = Category.BEER
        drink1.abv = 4.0
        drink1.price = 40
        drink1.volume = 500.0
        val drink2 = DrinkDefinition()
        drink2.name = "My wine"
        drink2.category = Category.WINE
        drink2.abv = 12.0
        drink2.price = 80
        drink2.volume = 200.0
        val drink3 = DrinkDefinition()
        drink3.name = "My shot"
        drink3.category = Category.COCKTAIL
        drink3.abv = 42.0
        drink3.price = 50
        drink3.volume = 20.0
        var drinks = listOf(
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
