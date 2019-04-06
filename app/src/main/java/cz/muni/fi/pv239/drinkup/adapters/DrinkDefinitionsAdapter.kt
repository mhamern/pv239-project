package cz.muni.fi.pv239.drinkup.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.pv239.drinkup.R
import cz.muni.fi.pv239.drinkup.activity.EditDrinkActivity
import cz.muni.fi.pv239.drinkup.database.entity.DrinkDefinition

class DrinkDefinitionsAdapter(private var context: Context, private var drinks: List<DrinkDefinition> =
    listOf()): RecyclerView.Adapter<DrinkDefinitionsAdapter.ViewHolder>() {

    fun refreshDrinks(drinks: List<DrinkDefinition>) {
        this.drinks = drinks
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.my_drinks_item_list, parent, false),
            context)
    }

    override fun getItemCount(): Int {
        return drinks.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(drinks[position])
    }

    class ViewHolder(itemView: View, private var context: Context): RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.my_drinks_name)
        var category: TextView = itemView.findViewById(R.id.my_drinks_category)
        var price: TextView = itemView.findViewById(R.id.my_drinks_price)
        var alcoholVolume: TextView = itemView.findViewById(R.id.my_drinks_alcohol)
        var volume: TextView = itemView.findViewById(R.id.my_drinks_volume)
        var icon: ImageView = itemView.findViewById(R.id.my_drinks_icon)
        var editButton: Button = itemView.findViewById(R.id.my_drinks_edit_button)

        fun bind(drink: DrinkDefinition) {
            name.text = drink.name
            category.text = drink.category.toString()
            price.text = drink.price.toString()
            alcoholVolume.text = "alcoholvolume"// context.applicationContext.getString(R.string.alcohol_with_percents, drink.abv)
            volume.text = "volume"//context.applicationContext.getString(R.string.drink_volume_with_millis, drink.volume)
            editButton.setOnClickListener {
                val intent = Intent(context, EditDrinkActivity::class.java)
                intent.putExtra("DRINK_TO_EDIT", drink)
                context.startActivity(intent)
            }
            // TODO: load icon

        }

    }
}