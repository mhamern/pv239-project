package cz.muni.fi.pv239.drinkup.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cz.muni.fi.pv239.drinkup.R
import cz.muni.fi.pv239.drinkup.database.AppDatabase
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_add_drink.*

class AddDrinkActivity: AppCompatActivity() {

    private var db: AppDatabase? = null
    private var addDrinksSubscription: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_drink)
        db = AppDatabase.getAppDatabase(this)
        createAppBar()

    }

    private fun createAppBar() {
        add_drink_toolbar.title = getString(R.string.add_drink)

        setSupportActionBar(add_drink_toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}