package cz.muni.fi.pv239.drinkup.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.widget.ArrayAdapter
import android.widget.Spinner
import cz.muni.fi.pv239.drinkup.R
import cz.muni.fi.pv239.drinkup.adapter.DrinkDefinitionsAdapter
import cz.muni.fi.pv239.drinkup.database.entity.Category
import cz.muni.fi.pv239.drinkup.database.entity.DrinkDefinition
import cz.muni.fi.pv239.drinkup.input.filter.InputFilterDecimalPointNumbersCount
import cz.muni.fi.pv239.drinkup.input.filter.InputFilterMinMax
import kotlinx.android.synthetic.main.activity_edit_drink.*

class EditDrinkDefinitionActivity : AppCompatActivity() {

    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_drink)
        if (savedInstanceState == null) {
            resolveMode()
            if (isEditMode) {
                initForEditMode()
            } else {
                initForCreateMode()
            }
            addFilters()
            if (savedInstanceState == null) {
                createAppBar()
            }
        }
    }

    private fun addFilters() {
        my_drinks_create_abv_input.filters = arrayOf<InputFilter>(InputFilterDecimalPointNumbersCount(2, 2))
        my_drinks_create_price_input.filters = arrayOf<InputFilter>(InputFilterDecimalPointNumbersCount(2, 2))
        my_drinks_create_volume_input.filters = arrayOf<InputFilter>(InputFilterMinMax(0, 999))
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
            my_drinks_toolbar.title = getString(R.string.edit_drink)
        } else {
            my_drinks_toolbar.title = getString(R.string.create_drink)
        }
        setSupportActionBar(my_drinks_toolbar)
    }


    private fun saveDrinkDefinition() {
        if (validateUserInput()) {
            onSaved()
        }
    }

    private fun updateDrinkDefinition() {
        if (validateUserInput()) {
            onSaved()
        }
    }

    private fun onSaved() {
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun validateUserInput(): Boolean {
        var isValid = true
        if (!validateName()) {
            isValid = false
        }
        if (!validateAbv()) {
            isValid = false
        }
        if (!validatePrice()) {
            isValid = false
        }
        if (!validateVolume()) {
            isValid = false
        }
        return isValid
    }

    private fun validateName(): Boolean {
        var isValid = true
        if (my_drinks_create_name_input.text.isNullOrEmpty()) {
            isValid = false
            my_drinks_create_name_input_layout.error = getString(R.string.error_drink_name_empty)
        }
        return isValid
    }

    private fun validatePrice(): Boolean {
        var isValid = true
        if (my_drinks_create_price_input.text.isNullOrEmpty()) {
            isValid = false
            my_drinks_create_price_input_layout.error = getString(R.string.error_drink_price_empty)
        }
        return isValid
    }

    private fun validateVolume(): Boolean {
        var isValid = true
        if (my_drinks_create_volume_input.text.isNullOrEmpty()) {
            isValid = false
            my_drinks_create_volume_input_layout.error = getString(R.string.error_drink_volume_empty)
        }

        return isValid

    }

    private fun validateAbv(): Boolean {
        var isValid = true
        if (my_drinks_create_abv_input.text.isNullOrEmpty()) {
            isValid = false
            my_drinks_create_abv_input_layout.error = getString(R.string.error_drink_abv_empty)
        }
        return isValid
    }
}