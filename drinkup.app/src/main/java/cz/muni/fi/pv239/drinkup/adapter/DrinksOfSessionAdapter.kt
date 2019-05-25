package cz.muni.fi.pv239.drinkup.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.pv239.drinkup.R
import cz.muni.fi.pv239.drinkup.database.entity.Drink

class DrinksOfSessionAdapter(
        private val context: Context,
        var drinks: List<Drink> = listOf()
): RecyclerView.Adapter<DrinksOfSessionAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       return ViewHolder(
               LayoutInflater.from(parent.context).inflate(R.layout.session_drink_list_item, parent, false),
               context
       )
       }

    fun refreshDrinks(drinks: List<Drink>){
        this.drinks = drinks
        notifyDataSetChanged()

    }

    override fun getItemCount(): Int {
       return drinks.size
       }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(drinks[position])
    }

    class ViewHolder(itemView: View, private var context: Context):RecyclerView.ViewHolder(itemView){
        var name: TextView = itemView.findViewById(R.id.session_drink_name)
       // var category: TextView = itemView.findViewById(R.id.session_drink_category)
        var price: TextView = itemView.findViewById(R.id.session_drink_price)
        var alcoholVolume: TextView = itemView.findViewById(R.id.session_drink_alcohol)
        //var volume: TextView = itemView.findViewById(R.id.session_drink_volume)

        fun bind(drink: Drink){
            name.text = drink.name
           // category.text = context.applicationContext.getString(R.string.category_attr, drink.category.toString())
            price.text = context.applicationContext.getString(R.string.price_attr, drink.price)
            alcoholVolume.text =  context.applicationContext.getString(R.string.alcohol_with_percents, drink.abv)
            //volume.text = context.applicationContext.getString(R.string.drink_volume_with_millis, drink.volume)
        }
    }
}