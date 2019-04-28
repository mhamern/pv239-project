package cz.muni.fi.pv239.drinkup.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import cz.muni.fi.pv239.drinkup.R
import cz.muni.fi.pv239.drinkup.adapter.DrinkingSessionsAdapter
import cz.muni.fi.pv239.drinkup.database.entity.DrinkingSession
import kotlinx.android.synthetic.main.fragment_history.*

class HistoryFragment : Fragment() {
    private lateinit var adapter: DrinkingSessionsAdapter

    private var listener: OnHistoryFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val myContext = context
            if (myContext != null) {
                adapter = DrinkingSessionsAdapter(myContext)
                drinking_session_list.adapter = adapter
                drinking_session_list.layoutManager = LinearLayoutManager(context)
                loadDrinkingSessions()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            loadDrinkingSessions()
        }
    }



    private fun loadDrinkingSessions(){
        val s1 = DrinkingSession()
        s1.title = "Session111"
        val s2 = DrinkingSession()
        s2.title = "Session1"
        val s3 = DrinkingSession()
        s3.title = "Session3"
        var sessions = listOf(
            s1,
            s2,
            s3,
            s1
        )
        populateList(sessions)
    }

    private fun populateList(drinkingSessions: List<DrinkingSession>?) {
        if (drinkingSessions == null) {
            return
        }
        adapter.refreshDrinkingSessions(drinkingSessions)
    }



    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onHistoryFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnHistoryFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnMyDrinksFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
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
    interface OnHistoryFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onHistoryFragmentInteraction(uri: Uri)
    }

    companion object {
        @JvmStatic
        fun newInstance(): HistoryFragment{
            return HistoryFragment()
        }

    }
}
