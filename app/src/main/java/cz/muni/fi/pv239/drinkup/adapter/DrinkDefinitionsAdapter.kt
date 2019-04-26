package cz.muni.fi.pv239.drinkup.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.pv239.drinkup.activity.EditDrinkDefinitionActivity
import cz.muni.fi.pv239.drinkup.database.entity.DrinkDefinition
import cz.muni.fi.pv239.drinkup.event.listener.EditDrinkDefinitionListener


class DrinkDefinitionsAdapter(
    private val context: Context,
    private val editListener: EditDrinkDefinitionListener,
    private var drinks: List<DrinkDefinition> =
        listOf()
): RecyclerView.Adapter<DrinkDefinitionsAdapter.ViewHolder>() {

    companion object {
        @JvmStatic val INTENT_EXTRA_EDIT_DRINK = "DRINK_TO_EDIT"
    }

    fun refreshDrinks(drinks: List<DrinkDefinition>) {
        this.drinks = drinks
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(cz.muni.fi.pv239.drinkup.R.layout.my_drinks_item_list, parent, false),
            context,
            editListener
            )
    }

    override fun getItemCount(): Int {
        return drinks.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(drinks[position])
    }

    class ViewHolder(itemView: View, private var context: Context, private var editListener: EditDrinkDefinitionListener): RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(cz.muni.fi.pv239.drinkup.R.id.my_drinks_name)
        var category: TextView = itemView.findViewById(cz.muni.fi.pv239.drinkup.R.id.my_drinks_category)
        var price: TextView = itemView.findViewById(cz.muni.fi.pv239.drinkup.R.id.my_drinks_price)
        var alcoholVolume: TextView = itemView.findViewById(cz.muni.fi.pv239.drinkup.R.id.my_drinks_alcohol)
        var volume: TextView = itemView.findViewById(cz.muni.fi.pv239.drinkup.R.id.my_drinks_volume)
        var icon: ImageView = itemView.findViewById(cz.muni.fi.pv239.drinkup.R.id.my_drinks_icon)
        var editButton: View = itemView.findViewById(cz.muni.fi.pv239.drinkup.R.id.my_drinks_edit_button)

        fun bind(drink: DrinkDefinition) {
            name.text = drink.name
            category.text = context.applicationContext.getString(cz.muni.fi.pv239.drinkup.R.string.category_attr, drink.category.toString())
            price.text = context.applicationContext.getString(cz.muni.fi.pv239.drinkup.R.string.price_attr, drink.price)
            alcoholVolume.text =  context.applicationContext.getString(cz.muni.fi.pv239.drinkup.R.string.alcohol_with_percents, drink.abv)
            volume.text = context.applicationContext.getString(cz.muni.fi.pv239.drinkup.R.string.drink_volume_with_millis, drink.volume)
            editButton.setOnClickListener {
                val intent = Intent(context, EditDrinkDefinitionActivity::class.java)
                intent.putExtra(INTENT_EXTRA_EDIT_DRINK, drink)
                editListener.onEditRequested(intent)
            }
            // TODO: load icon
        }

    }
}