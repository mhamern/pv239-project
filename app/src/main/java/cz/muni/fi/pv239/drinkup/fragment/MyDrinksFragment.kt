package cz.muni.fi.pv239.drinkup.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import cz.muni.fi.pv239.drinkup.R
import cz.muni.fi.pv239.drinkup.activity.EditDrinkActivity
import cz.muni.fi.pv239.drinkup.adapters.DrinksAdapter
import cz.muni.fi.pv239.drinkup.database.entity.Drink
import kotlinx.android.synthetic.main.fragment_my_drinks.*


class MyDrinksFragment : Fragment() {

    private lateinit var adapter: DrinksAdapter

    private var listenerMyDrinks: OnMyDrinksFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_my_drinks, container, false)
        my_drinks_list.adapter = adapter

        my_drinks_create_button.setOnClickListener {
            var intent = Intent(it.context, EditDrinkActivity::class.java) // todo decide if reuse or use new activity for slightly different use case
            it.context.startActivity(intent)
        }
        return view
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


    fun loadDrinks() {
        // todo load from DB
    }

    private fun populateList(drinks: List<Drink>?) {
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
