package cz.muni.fi.pv239.drinkup.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import cz.muni.fi.pv239.drinkup.R
import cz.muni.fi.pv239.drinkup.adapters.DrinkDefinitionsAdapter
import cz.muni.fi.pv239.drinkup.database.entity.Category
import cz.muni.fi.pv239.drinkup.database.entity.DrinkDefinition
import kotlinx.android.synthetic.main.activity_edit_drink.*

class EditDrinkDefinitionActivity : AppCompatActivity() {

    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_drink)
        resolveMode()
        if (isEditMode) {
            initForEditMode()
        } else {
            initForCreateMode()
        }

        if (savedInstanceState == null) {
            createAppBar()
        }
    }

    private fun resolveMode() {
        isEditMode = this.intent.hasExtra(DrinkDefinitionsAdapter.INTENT_EXTRA_EDIT_DRINK)
    }

    private fun initForEditMode() {
        val drinkDefinition = this.intent.getParcelableExtra<DrinkDefinition>(DrinkDefinitionsAdapter.INTENT_EXTRA_EDIT_DRINK)
        my_drinks_create_name_input.setText(drinkDefinition.name)
        my_drinks_create_price_input.setText(drinkDefinition.price.toString())
        my_drinks_create_abv_input.setText(drinkDefinition.abv.toString())
        my_drinks_create_volume_input.setText(drinkDefinition.volume.toString())
        val spinner: Spinner = my_drinks_create_category_spinner
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, Category.values())
        spinner.setSelection(drinkDefinition.category.ordinal)
        my_drinks_save_button.setOnClickListener {
            updateDrinkDefinition()
        }
    }


    private fun initForCreateMode() {
        val spinner: Spinner = my_drinks_create_category_spinner
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, Category.values())
        my_drinks_save_button.setOnClickListener {
            saveDrinkDefinition()
        }

    }

    private fun createAppBar() {
        if (isEditMode) {
            my_drinks_toolbar.title = "Edit Drink"
        } else {
            my_drinks_toolbar.title = "Create Drink"
        }
        setSupportActionBar(my_drinks_toolbar)
    }


    private fun saveDrinkDefinition() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun updateDrinkDefinition() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}