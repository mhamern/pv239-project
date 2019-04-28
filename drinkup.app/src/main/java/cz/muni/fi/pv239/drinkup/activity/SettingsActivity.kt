package cz.muni.fi.pv239.drinkup.activity

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import com.takisoft.preferencex.PreferenceFragmentCompat
import androidx.appcompat.app.AppCompatActivity
import com.takisoft.preferencex.EditTextPreference
import cz.muni.fi.pv239.drinkup.input.filter.InputFilterMinMax
import kotlinx.android.synthetic.main.settings.*






class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(cz.muni.fi.pv239.drinkup.R.layout.settings)
        supportFragmentManager.beginTransaction()
            .replace(cz.muni.fi.pv239.drinkup.R.id.settings_fragment_container, MySettingsFragment())
            .commit()
        setupActionBar()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun setupActionBar() {
        setSupportActionBar(settings_toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }
}

class MySettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(cz.muni.fi.pv239.drinkup.R.xml.preferences, rootKey)
        val etPref = findPreference("pref_weight") as EditTextPreference
        etPref.editText.filters = arrayOf(InputFilterMinMax(0, 300))
        bindPreferenceSummaryToValue(findPreference("pref_gender"))
        bindPreferenceSummaryToValue(findPreference("pref_weight"))
    }

    companion object {
        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
            val stringValue = value.toString()

            if (preference is EditTextPreference) {
                if (preference.text.isNullOrEmpty()) {
                    preference.setSummary("0")
                }
                preference.setSummary(stringValue)
            }
            if (preference is ListPreference) {
                val index = preference.findIndexOfValue(stringValue)
                preference.setSummary(
                    if (index >= 0)
                        preference.entries[index]
                    else
                        null
                )
            }
            true
        }

        private fun bindPreferenceSummaryToValue(preference: Preference) {
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener
            sBindPreferenceSummaryToValueListener.onPreferenceChange(
                preference,
                PreferenceManager.getDefaultSharedPreferences(preference.context)
                    .getString(preference.key, "")
            )
        }
    }
}
