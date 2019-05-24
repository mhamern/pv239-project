package cz.muni.fi.pv239.drinkup.fragment

import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import cz.muni.fi.pv239.drinkup.R
import cz.muni.fi.pv239.drinkup.adapter.AchievementsAdapter
import cz.muni.fi.pv239.drinkup.database.entity.Achievement
import kotlinx.android.synthetic.main.fragment_achievements.*

class AchievementsFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_achievements, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        var beer = sharedPreferences.getInt("ach_drinkBeer", 0)
        var shots = sharedPreferences.getInt("ach_drinkShot", 0)
        val achievements = listOf(
            Achievement(getString(R.string.ach_drink5beer), (beer.toDouble()/5*100).toInt(), beer, 5),
            Achievement(getString(R.string.ach_drink20beer), (beer.toDouble()/20*100).toInt(), beer, 20),
            Achievement(getString(R.string.ach_drink100beer), (beer.toDouble()/100*100).toInt(), beer, 100),
            Achievement(getString(R.string.ach_drink5shots), (shots.toDouble()/5*100).toInt(), shots, 5),
            Achievement(getString(R.string.ach_drink20shots), (shots.toDouble()/20*100).toInt(), shots, 20)
        )
        recycler_view_achievements.layoutManager = LinearLayoutManager(this.context)
        recycler_view_achievements.adapter = AchievementsAdapter(achievements)
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
    interface OnAchievementsFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onAchievementsFragmentInteraction(uri: Uri)
    }

}
