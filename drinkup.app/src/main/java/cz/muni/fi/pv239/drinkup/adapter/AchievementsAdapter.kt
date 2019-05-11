package cz.muni.fi.pv239.drinkup.adapter

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.alpha
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.pv239.drinkup.R
import cz.muni.fi.pv239.drinkup.database.entity.Achievement
import kotlinx.android.synthetic.main.achievement.view.*

class AchievementsAdapter(val achievements: List<Achievement>): RecyclerView.Adapter<AchievementsAdapter.AchievementViewholder>() {
    override fun onBindViewHolder(holder: AchievementViewholder, position: Int) {
        var achievement = achievements[position]
        holder.view.text_achievement.text = achievement.text
        if (achievement.actual < achievement.max) {
            holder.view.text_achievement2.text = achievement.actual.toString() + "/" + achievement.max.toString()
        } else {
            holder.view.text_achievement2.text = achievement.max.toString() + "/" + achievement.max.toString()
        }
        holder.view.progress_achievement.progress = achievement.progress
    }

    class AchievementViewholder(val view: View): RecyclerView.ViewHolder(view)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewholder {
        return AchievementViewholder(
            LayoutInflater.from(parent.context).inflate(R.layout.achievement, parent, false)
        )
    }

    override fun getItemCount() = achievements.size
}