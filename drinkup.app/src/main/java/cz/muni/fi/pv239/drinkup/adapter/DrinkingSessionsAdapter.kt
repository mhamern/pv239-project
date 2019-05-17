package cz.muni.fi.pv239.drinkup.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.pv239.drinkup.R
import cz.muni.fi.pv239.drinkup.activity.DrinkingSessionDetailActivity
import cz.muni.fi.pv239.drinkup.database.entity.DrinkingSession
import cz.muni.fi.pv239.drinkup.event.listener.OnDrinkingSessionDetailListener

class DrinkingSessionsAdapter(
    private val context: Context,
    private val onSessionDetailListener: OnDrinkingSessionDetailListener,
    private var drinkingSessions: List<DrinkingSession> =
        listOf()
): RecyclerView.Adapter<DrinkingSessionsAdapter.ViewHolder>(){

    companion object {
        @JvmStatic val INTENT_EXTRA_DRINKING_SESSION = "DRINKING_SESSION"
    }

    fun refreshDrinkingSessions(drinkingSessions: List<DrinkingSession>){
        this.drinkingSessions = drinkingSessions
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(drinkingSessions[position])
    }

    override fun getItemCount(): Int {
        return drinkingSessions.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.session_list_item, parent, false),
                context,
                onSessionDetailListener
        )
    }

    class ViewHolder(itemView: View, private var context: Context, private var onSessionDetailListener: OnDrinkingSessionDetailListener): RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(cz.muni.fi.pv239.drinkup.R.id.session_name)
        var totalPrice: TextView = itemView.findViewById(cz.muni.fi.pv239.drinkup.R.id.session_total_price)

        fun bind(drinkingSession: DrinkingSession){
            name.text = drinkingSession.title
            //val price = drinkingSession.drinks.sumByDouble { it.price }
            totalPrice.text = context.applicationContext.getString(cz.muni.fi.pv239.drinkup.R.string.total_price_of_session, 0.0)

            itemView.setOnClickListener{
                val intent = Intent(context, DrinkingSessionDetailActivity::class.java)
                intent.putExtra(INTENT_EXTRA_DRINKING_SESSION, drinkingSession)
                onSessionDetailListener.onClickOnSession(intent)
            }
        }
    }
}