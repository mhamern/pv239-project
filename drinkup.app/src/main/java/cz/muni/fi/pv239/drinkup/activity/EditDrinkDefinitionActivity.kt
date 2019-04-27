package cz.muni.fi.pv239.drinkup.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.room.RxRoom
import cz.muni.fi.pv239.drinkup.R
import cz.muni.fi.pv239.drinkup.adapter.DrinkDefinitionsAdapter
import cz.muni.fi.pv239.drinkup.database.AppDatabase
import cz.muni.fi.pv239.drinkup.database.dao.DrinkDefinitionDao
import cz.muni.fi.pv239.drinkup.database.entity.Category
import cz.muni.fi.pv239.drinkup.database.entity.DrinkDefinition
import cz.muni.fi.pv239.drinkup.input.filter.InputFilterDecimalPointNumbersCount
import cz.muni.fi.pv239.drinkup.input.filter.InputFilterMinMax
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_edit_drink.*

class EditDrinkDefinitionActivity : AppCompatActivity() {

    private var isEditMode = false
    private var db: AppDatabase? = null
    private var drinkDefDao: DrinkDefinitionDao? = null
    private var saveDrinkDefinitionSubscription: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(cz.muni.fi.pv239.drinkup.R.layout.activity_edit_drink)
        if (savedInstanceState == null) {
            db = AppDatabase.getAppDatabase(this)
            drinkDefDao = db?.drinkDefinitionDao()
            resolveMode()
            if (isEditMode) {
                initForEditMode()
            } else {
                initForCreateMode()
            }
            addFilters()
            createAppBar()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(cz.muni.fi.pv239.drinkup.R.menu.edit_drink_definition_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit_drink_definition_menu_save -> {
                if (isEditMode) {
                    updateDrinkDefinition()
                }
                else {
                    saveDrinkDefinition()
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        saveDrinkDefinitionSubscription?.dispose()
    }

    private fun addFilters() {
        my_drinks_create_abv_input.filters = arrayOf<InputFilter>(
            InputFilterDecimalPointNumbersCount(2, 2, this)
        )
        my_drinks_create_price_input.filters = arrayOf<InputFilter>(
            InputFilterDecimalPointNumbersCount(2, 2, this)
        )
        my_drinks_create_volume_input.filters = arrayOf<InputFilter>(
            InputFilterMinMax(0, 1000)
        )
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
    }


    private fun initForCreateMode() {
        val spinner: Spinner = my_drinks_create_category_spinner
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, Category.values())
    }

    private fun createAppBar() {
        if (isEditMode) {
            my_drinks_toolbar.title = getString(cz.muni.fi.pv239.drinkup.R.string.edit_drink)
        } else {
            my_drinks_toolbar.title = getString(cz.muni.fi.pv239.drinkup.R.string.create_drink)
        }
        setSupportActionBar(my_drinks_toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true);
        }
    }


    private fun saveDrinkDefinition() {
        if (validateUserInput()) {
            saveDrinkDefinitionSubscription = RxRoom.createFlowable(db)
                .observeOn(Schedulers.io())
                .map { db?.drinkDefinitionDao()?.insertDrinkDefiniton(createDrinkDefinitionFromInput()) ?: error("DB Error")}
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    onSaved()
                }
        }
    }


    private fun updateDrinkDefinition() {
        if (validateUserInput()) {
            saveDrinkDefinitionSubscription = RxRoom.createFlowable(db)
                .observeOn(Schedulers.io())
                .map { db?.drinkDefinitionDao()?.updateDrinkDefinition(createDrinkDefinitionFromInput()) ?: error("DB Error")}
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    onSaved()
                }
        }
    }

    private fun onSaved() {
        Toast.makeText(this, "Changes were successfully saved", Toast.LENGTH_SHORT).show()
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
            my_drinks_create_name_input_layout.error = getString(cz.muni.fi.pv239.drinkup.R.string.error_drink_name_empty)
        }
        return isValid
    }

    private fun validatePrice(): Boolean {
        var isValid = true
        if (my_drinks_create_price_input.text.isNullOrEmpty()) {
            isValid = false
            my_drinks_create_price_input_layout.error = getString(cz.muni.fi.pv239.drinkup.R.string.error_drink_price_empty)
        }
        return isValid
    }

    private fun validateVolume(): Boolean {
        var isValid = true
        if (my_drinks_create_volume_input.text.isNullOrEmpty()) {
            isValid = false
            my_drinks_create_volume_input_layout.error = getString(cz.muni.fi.pv239.drinkup.R.string.error_drink_volume_empty)
        }

        return isValid

    }

    private fun validateAbv(): Boolean {
        var isValid = true
        if (my_drinks_create_abv_input.text.isNullOrEmpty()) {
            isValid = false
            my_drinks_create_abv_input_layout.error = getString(cz.muni.fi.pv239.drinkup.R.string.error_drink_abv_empty)
        }
        return isValid
    }

    private fun createDrinkDefinitionFromInput(): DrinkDefinition {
        if (isEditMode) {
            val drinkDefToUpdate = this.intent.getParcelableExtra<DrinkDefinition>(DrinkDefinitionsAdapter.INTENT_EXTRA_EDIT_DRINK)
            drinkDefToUpdate.name = my_drinks_create_name_input.text.toString()
            drinkDefToUpdate.abv =  my_drinks_create_abv_input.text.toString().toDouble()
            drinkDefToUpdate.price = my_drinks_create_price_input.text.toString().toDouble()
            drinkDefToUpdate.category = Category.values()[my_drinks_create_category_spinner.selectedItemPosition]
            drinkDefToUpdate.volume = my_drinks_create_volume_input.text.toString().toInt()
            return drinkDefToUpdate
        }
        else {
            return DrinkDefinition(
                name =  my_drinks_create_name_input.text.toString(),
                abv = my_drinks_create_abv_input.text.toString().toDouble(),
                price = my_drinks_create_price_input.text.toString().toDouble(),
                category = Category.values()[my_drinks_create_category_spinner.selectedItemPosition],
                volume =  my_drinks_create_volume_input.text.toString().toInt())
        }
    }
}