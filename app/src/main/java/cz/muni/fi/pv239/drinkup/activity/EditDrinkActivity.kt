package cz.muni.fi.pv239.drinkup.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cz.muni.fi.pv239.drinkup.R
import kotlinx.android.synthetic.main.activity_edit_drink.*

class EditDrinkActivity : AppCompatActivity() {

    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_drink)
        resolveMode()
        if (savedInstanceState == null) {
            createAppBar()
        }
    }

    private fun resolveMode() {
        this.isEditMode = true
    }

    private fun createAppBar() {
        if (isEditMode) {
            my_drinks_toolbar.title = "Edit Drink"
        } else {
            my_drinks_toolbar.title = "Create Drink"
        }
        setSupportActionBar(my_drinks_toolbar)

    }
}