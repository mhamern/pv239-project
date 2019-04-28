package cz.muni.fi.pv239.drinkup.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.pv239.drinkup.R
import cz.muni.fi.pv239.drinkup.database.entity.Session

class SessionsAdapter(
    private val context: Context,
    private var sessions: List<Session> =
        listOf()
): RecyclerView.Adapter<SessionsAdapter.ViewHolder>(){

    fun refreshDrinkingSessions(sessions: List<Session>){
        this.sessions = sessions
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(sessions[position])
    }

    override fun getItemCount(): Int {
        return sessions.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionsAdapter.ViewHolder {
        return SessionsAdapter.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.session_list_item, parent, false),
            context
        )
    }

    class ViewHolder(itemView: View, private var context: Context): RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(cz.muni.fi.pv239.drinkup.R.id.session_name)
        var totalPrice: TextView = itemView.findViewById(cz.muni.fi.pv239.drinkup.R.id.session_total_price)

        fun bind(session: Session){
            name.text = session.title
            val price = session.drinks.sumByDouble { it.price }
            totalPrice.text = context.applicationContext.getString(cz.muni.fi.pv239.drinkup.R.string.total_price_of_session, price)

        }
    }
}