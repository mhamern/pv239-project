package cz.muni.fi.pv239.drinkup.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.pv239.drinkup.R
import cz.muni.fi.pv239.drinkup.activity.EditDrinkDefinitionActivity
import cz.muni.fi.pv239.drinkup.database.entity.DrinkDefinition

class DrinkDefinitionsAdapter(private var context: Context, private var drinks: List<DrinkDefinition> =
    listOf()): RecyclerView.Adapter<DrinkDefinitionsAdapter.ViewHolder>() {

    companion object {
        @JvmStatic val INTENT_EXTRA_EDIT_DRINK = "DRINK_TO_EDIT"
    }

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
        var editButton: View = itemView.findViewById(R.id.my_drinks_edit_button)

        fun bind(drink: DrinkDefinition) {
            name.text = drink.name
            category.text = context.applicationContext.getString(R.string.category_attr, drink.category.toString())
            price.text = context.applicationContext.getString(R.string.price_attr, drink.price)
            alcoholVolume.text =  context.applicationContext.getString(R.string.alcohol_with_percents, drink.abv)
            volume.text = context.applicationContext.getString(R.string.drink_volume_with_millis, drink.volume)
            editButton.setOnClickListener {
                val intent = Intent(context, EditDrinkDefinitionActivity::class.java)
                intent.putExtra(INTENT_EXTRA_EDIT_DRINK, drink)
                context.startActivity(intent)
            }
            // TODO: load icon

        }

    }
}