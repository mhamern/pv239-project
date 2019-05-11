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
        val achievements = listOf(
            Achievement(getString(R.string.ach_drink5beer), (sharedPreferences.getInt("ach_drink5beer", 0).toDouble()/5*100).toInt(), sharedPreferences.getInt("ach_drink5beer", 0), 5),
            Achievement(getString(R.string.ach_drink20beer), (sharedPreferences.getInt("ach_drink20beer", 0).toDouble()/5*100).toInt(), sharedPreferences.getInt("ach_drink20beer", 0), 20),
            Achievement(getString(R.string.ach_drink100beer), (sharedPreferences.getInt("ach_drink100beer", 0).toDouble()/5*100).toInt(), sharedPreferences.getInt("ach_drink100beer", 0), 100),
            Achievement(getString(R.string.ach_drink5shots), (sharedPreferences.getInt("ach_drink5shots", 0).toDouble()/5*100).toInt(), sharedPreferences.getInt("ach_drink5shots", 0), 5),
            Achievement(getString(R.string.ach_drink20shots), (sharedPreferences.getInt("ach_drink20shots", 0).toDouble()/5*100).toInt(), sharedPreferences.getInt("ach_drink20shots", 0), 20)
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
