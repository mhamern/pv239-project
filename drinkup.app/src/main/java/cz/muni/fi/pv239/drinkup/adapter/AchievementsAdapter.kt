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
        holder.view.progress_achievement.progress = achievement.progress
        if(achievement.progress >= 100) {
            holder.view.linearLayout_achievement.background.setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY)
        } else {
            holder.view.linearLayout_achievement.background.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY)
        }
    }

    class AchievementViewholder(val view: View): RecyclerView.ViewHolder(view)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewholder {
        return AchievementViewholder(
            LayoutInflater.from(parent.context).inflate(R.layout.achievement, parent, false)
        )
    }

    override fun getItemCount() = achievements.size
}